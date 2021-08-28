package io.ktor.swagger.experimental

import com.fasterxml.jackson.module.kotlin.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.response.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import kotlinx.coroutines.*
import java.lang.reflect.*
import java.lang.reflect.Type
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

class HttpException(val code: HttpStatusCode, val description: String = code.description) : RuntimeException(description)

fun httpException(code: HttpStatusCode, message: String = code.description): Nothing = throw HttpException(code, message)
fun httpException(code: Int, message: String = "Error $code"): Nothing = throw HttpException(HttpStatusCode(code, message))
@Suppress("unused")
inline fun <T> T.verifyParam(name: String, callback: (T) -> Boolean): T {
    if (!callback(this)) throw IllegalArgumentException(name); return this
}

inline fun <T> T.checkRequest(cond: Boolean, callback: () -> String) {
    if (!cond) httpException(HttpStatusCode.BadRequest, callback())
}

interface SwaggerBaseApi

interface SwaggerBaseServer

class ApplicationCallContext(val call: ApplicationCall) : CoroutineContext.Element {
    object KEY : CoroutineContext.Key<ApplicationCallContext>

    override val key: CoroutineContext.Key<*> = KEY
}

@Suppress("unused")
suspend fun SwaggerBaseServer.call(): ApplicationCall {
    return coroutineContext[ApplicationCallContext.KEY]?.call ?: error("ApplicationCall not available")
}

annotation class Method(val method: String)
annotation class Body(val name: String)
annotation class Header(val name: String)
annotation class Query(val name: String)
annotation class Path(val name: String) // Reused
annotation class FormData(val name: String)
annotation class Auth(vararg val auths: String)

//interface FeatureClass
//annotation class Feature(val clazz: KClass<out FeatureClass>)

inline fun <reified T : SwaggerBaseApi> createClient(client: HttpClient, rootUrl: String): T =
    createClient(T::class.java, client, rootUrl)

fun <T : SwaggerBaseApi> createClient(clazz: Class<T>, client: HttpClient, rootUrl: String): T {
    val rootUrlTrim = rootUrl.trimEnd('/')
    val apiClass = ApiClass.parse(clazz)
    var authContext = LinkedHashMap<String, String>()

    return Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { proxy, method, args ->
        val info = apiClass.getInfo(method) ?: error("Can't find method $method")
        val rparams = info.params.zip(args.slice(0 until info.params.size)).map { ApiClass.ApiParamInfoValue<Any?>(it.first as ApiClass.ApiParamInfo<Any?>, it.second) }.associateBy { it.name }

        //val params = method.parameters
        val cont = args.lastOrNull() as? Continuation<Any>?
            ?: throw RuntimeException("Just implemented suspend functions")

        val continuationReturnType = method.genericParameterTypes.firstOrNull()?.extractFirstGenericType()

        val realReturnType = continuationReturnType ?: method.returnType

        val pathPattern = info.path

        val pathReplaced = pathPattern.replace { "${rparams[it]?.value}" }

        kotlinx.coroutines.GlobalScope.apply {
            launch {
                try {
                    val fullUrl = "$rootUrlTrim/$pathReplaced"
                    val res = client.request<HttpStatement>(fullUrl) {
                        this.method = HttpMethod(info.httpMethod)
                        val body = linkedMapOf<String, Any?>()
                        val formData = linkedMapOf<String, Any?>()
                        for (param in rparams.values) {
                            when (param.source) {
                                Source.QUERY -> parameter(param.name, "${param.value}")
                                Source.HEADER -> header(param.name, "${param.value}")
                                Source.BODY -> body[param.name] = param.value
                                Source.FORM_DATA -> formData[param.name] = param.value
                                Source.PATH -> { /* do nothing for now */
                                }
                            }
                        }
                        if (body.isNotEmpty()) {
                            this.contentType(io.ktor.http.ContentType.Application.Json)
                            this.body =
                                ByteArrayContent(Json.stringify(body).toByteArray(Charsets.UTF_8))
                        }
                        if (formData.isNotEmpty()) {
                            this.contentType(io.ktor.http.ContentType.Application.FormUrlEncoded)
                            this.body =
                                ByteArrayContent(formData.map { it.key to it.value.toString() }
                                    .formUrlEncode().toByteArray(Charsets.UTF_8))
                        }
                    }
                    if (res.execute().status.value < 400) {
                        cont.resume(Json.parse(res.execute().readText(), realReturnType))
                    } else {
                        throw HttpExceptionWithContent(
                            res.execute().status,
                            res.execute().readText()
                        )
                    }
                } catch (e: Throwable) {
                    cont.resumeWithException(e)
                }
            }
        }
        COROUTINE_SUSPENDED
    } as T
}

class HttpExceptionWithContent(val code: HttpStatusCode, val content: String) :
    RuntimeException("HTTP ERROR $code : $content")

fun Routing.registerRoutes(server: SwaggerBaseServer) {
    val clazz = ApiClass.parse(server::class.java)

    for (method in clazz.methods) {
        authenticateIfNotEmpty(method.auths) {
            route(method.path.pathPattern, HttpMethod(method.httpMethod)) {
                handle {
                    val args = arrayListOf<Any?>()
                    for (param in method.params) {
                        args += param.get(call)
                    }
                    withContext(ApplicationCallContext(call)) {
                        val result = method.method.invokeSuspend(server, args)
                        call.respondText(Json.stringify(result ?: Any()), ContentType.Application.Json)
                    }
                }
            }
        }
    }
}

enum class Source {
    BODY, QUERY, FORM_DATA, HEADER, PATH
}

fun Route.authenticateIfNotEmpty(configurations: List<String>, optional: Boolean = false, build: Route.() -> Unit): Route {
    return if (configurations.isEmpty()) {
        build()
        this
    } else {
        authenticate(*configurations.toTypedArray(), optional = optional, build = build)
    }
}


class ApiClass(val clazz: Class<*>, val methods: List<ApiMethodInfo>) {
    val methodsBySignature = methods.associateBy { it.methodSignature }

    fun getInfo(method: java.lang.reflect.Method) = methodsBySignature[method.signature]

    companion object {
        fun parse(clazz: Class<*>): ApiClass {
            val imethods = arrayListOf<ApiMethodInfo>()
            for (method in clazz.methods) {
                val path = method.getAnnotationInAncestors(Path::class.java)?.name
                val httpMethod = method.getAnnotationInAncestors(Method::class.java)?.method ?: "GET"
                //println("METHOD: $method, $path")
                if (path != null) {
                    val params = arrayListOf<ApiParamInfo<*>>()
                    for ((ptype, annotations) in method.parameterTypes.zip(method.parameterAnnotationsInAncestors)) {
                        // Skip the continuation last argument!
                        if (ptype.isAssignableFrom(Continuation::class.java)) continue

                        val body = annotations.filterIsInstance<Body>().firstOrNull()?.name
                        val query = annotations.filterIsInstance<Query>().firstOrNull()?.name
                        val formData = annotations.filterIsInstance<FormData>().firstOrNull()?.name
                        val header = annotations.filterIsInstance<Header>().firstOrNull()?.name
                        val ppath = annotations.filterIsInstance<Path>().firstOrNull()?.name

                        val source = when {
                            body != null -> Source.BODY
                            query != null -> Source.QUERY
                            formData != null -> Source.FORM_DATA
                            header != null -> Source.HEADER
                            ppath != null -> Source.PATH
                            else -> Source.QUERY
                        }
                        val rname = body ?: query ?: formData ?: header ?: ppath ?: "unknown"

                        //println("   - $ptype, ${annotations.toList()}")

                        params += ApiParamInfo(source, rname, ptype)
                    }

                    //println("METHOD: $instance, $method, $httpMethod, $path")
                    //for (param in params) println("  - $param")

                    val auths = method.getAnnotationInAncestors(Auth::class.java)?.auths?.toList() ?: listOf()

                    imethods += ApiMethodInfo(method, PathPattern(path.trim('/')), httpMethod, auths, params)
                }
            }
            return ApiClass(clazz, imethods)
        }
    }

    class ApiMethodInfo(val method: java.lang.reflect.Method, val path: PathPattern, val httpMethod: String, val auths: List<String>, val params: List<ApiParamInfo<*>>) {
        val methodSignature = method.signature
    }

    data class ApiParamInfo<T>(val source: Source, val name: String, val type: Class<T>) {
        suspend fun get(call: ApplicationCall): T {
            return call.getTyped(source, name, type)
        }
    }

    data class ApiParamInfoValue<T>(val info: ApiParamInfo<T>, val value: T) {
        val source get() = info.source
        val type get() = info.type
        val name get() = info.name
    }

    class PathPattern(val pathPattern: String) {
        companion object {
            val PARAM_REGEX = Regex("\\{(\\w*)\\}")
        }

        val pathNames by lazy { PARAM_REGEX.findAll(pathPattern).map { it.groupValues[1] }.toList() }
        val pathRegex by lazy { Regex(replace { "(\\w+)" }) }

        fun replace(replacer: (name: String) -> String): String {
            return pathPattern.replace(PARAM_REGEX) { mr -> replacer(mr.groupValues[1]) }
        }

        fun extract(path: String): List<String> {
            return pathRegex.find(path)?.groupValues?.drop(1) ?: listOf()
        }
    }
}

data class MethodSignature(val name: String, val types: List<Class<*>>)

val java.lang.reflect.Method.signature get() = MethodSignature(name, parameterTypes.toList())

object Json {
    @PublishedApi
    internal val objectMapper = jacksonObjectMapper()

    fun <T> convert(value: Any?, clazz: Class<T>): T = objectMapper.convertValue(value, clazz)
    fun <T> parse(str: String, clazz: Class<T>): T = objectMapper.readValue(str, clazz)
    fun <T> stringify(value: T): String = objectMapper.writeValueAsString(value)
}

//inline fun <reified T> ApplicationCall.getTyped(source: String, name: String): T =
//        objectMapper.convertValue(getRaw(source, name), T::class.java)
suspend fun <T> ApplicationCall.getTyped(source: Source, name: String, clazz: Class<T>): T {
    return Json.convert(getRaw(source, name), clazz)
}

suspend fun <T> ApplicationCall.getTypedOrNull(source: Source, name: String, clazz: Class<T>): T? =
    getRaw(source, name)?.let { Json.convert(it, clazz) }

suspend fun ApplicationCall.getRaw(source: Source, name: String): Any? {
    return when (source) {
        Source.PATH -> this.parameters.get(name)
        Source.QUERY -> this.request.queryParameters.get(name)
        Source.BODY -> this.getCachedUntypedBody()[name]
        Source.FORM_DATA -> TODO()
        Source.HEADER -> this.request.header(name)
    }
}

val CACHED_BODY_KEY = AttributeKey<Any>("CACHED_BODY_KEY")

inline fun <T : Any> Attributes.computeIfAbsentInline(key: AttributeKey<T>, block: () -> T): T {
    if (!this.contains(key)) {
        this.put(key, block())
    }
    return this[key]
}

private suspend fun ApplicationCall.getCachedUntypedBody(): Map<String, Any?> {
    return attributes.computeIfAbsentInline(CACHED_BODY_KEY) {
        Json.parse(this@getCachedUntypedBody.receive(), HashMap::class.java)
    } as Map<String, Any?>
}

///////////////////////////////////////////////////
// Reflection Tools
///////////////////////////////////////////////////

val java.lang.Class<*>.allTypes: Set<Class<*>>
    get() {
        val types = LinkedHashSet<Class<*>>()
        val explore = arrayListOf(this)
        while (explore.isNotEmpty()) {
            val item = explore.removeAt(explore.size - 1)
            types += item
            explore += item.superclass
            explore += item.interfaces
        }
        return types
    }

val java.lang.reflect.Method.parameterAnnotationsInAncestors: List<List<Annotation>>
    get() {
        val allMethods = this.declaringClass.allTypes.map {
            try {
                it.getDeclaredMethod(name, *parameterTypes) ?: null
            } catch (e: NoSuchMethodException) {
                null
            }
        }.filterNotNull()
        val out = Array<ArrayList<Annotation>>(parameterTypes.size) { arrayListOf() }.toList()
        for (method in allMethods) {
            for ((index, annotations) in method.parameterAnnotations.withIndex()) {
                out[index] += annotations
            }
        }
        return out
    }

suspend fun java.lang.reflect.Method.invokeSuspend(obj: Any?, args: List<Any?>): Any? = suspendCoroutine { c ->
    val method = this@invokeSuspend

    val lastParam = method.parameterTypes.lastOrNull()
    val margs = java.util.ArrayList(args)

    if (lastParam != null && lastParam.isAssignableFrom(Continuation::class.java)) {
        margs += c
    }
    try {
        val result = method.invoke(obj, *margs.toTypedArray())
        if (result != COROUTINE_SUSPENDED) {
            c.resume(result)
        }
    } catch (e: InvocationTargetException) {
        c.resumeWithException(e.targetException)
    } catch (e: Throwable) {
        c.resumeWithException(e)
    }
}

fun <T : Annotation> java.lang.reflect.Method.getAnnotationInAncestors(clazz: Class<T>): T? {
    val res = this.getAnnotation(clazz) ?: this.getDeclaredAnnotation(clazz)
    if (res != null) return res

    // Try interfaces
    for (ifc in this.declaringClass.interfaces) {
        return ignoreErrors { ifc?.getMethod(name, *parameterTypes)?.getAnnotationInAncestors(clazz) } ?: continue
    }

    // Try ancestor
    return ignoreErrors { this.declaringClass.superclass?.getMethod(name, *parameterTypes) }?.getAnnotationInAncestors(
        clazz
    )
}

inline fun <T> ignoreErrors(callback: () -> T): T? = try {
    callback()
} catch (e: Throwable) {
    null
}

fun Type.extractFirstGenericType(): Class<*> {
    if (this is ParameterizedType) {
        return this.actualTypeArguments.first().extractFirstGenericType()
    }
    if (this is WildcardType) {
        val tt = this.lowerBounds.firstOrNull() ?: this.upperBounds.firstOrNull()
        ?: error("WildcardType without lower/upper bounds")
        return tt.extractFirstGenericType()
    }
    if (this is Class<*>) {
        return this
    }
    error("Couldn't find right generic type")
}

suspend inline fun <reified T : Any> ApplicationCall.getBodyParam(name: String, noinline default: () -> T = { error("mandatory $name") }): T =
    getTypedOrNull(Source.BODY, name, T::class.java) ?: default()

suspend inline fun <reified T : Any> ApplicationCall.getPath(name: String, noinline default: () -> T = { error("mandatory $name") }): T =
    getTypedOrNull(Source.PATH, name, T::class.java) ?: default()

suspend inline fun <reified T : Any> ApplicationCall.getQuery(name: String, noinline default: () -> T = { error("mandatory $name") }): T =
    getTypedOrNull(Source.QUERY, name, T::class.java) ?: default()
