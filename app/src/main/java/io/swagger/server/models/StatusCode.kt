package io.swagger.server.models

/**
 * The status code.
 */
enum class StatusCode(val value: Int) {
    TIMEOUT_WINDOW_UPDATE(1001) {
        override fun message(): String {
            TODO("not implemented")
        }
    },

    ARGUMENT_MISSING(3001) {

        override fun message(): String {
            TODO("not implemented")
        }
    },

    INTERACTION_KEY(5001) {

        override fun message(): String {
            TODO("not implemented")
        }
    },

    OBJECT_NOT_FOUND(6000) {

        override fun message(): String {
            return "Object not found"
        }
    };

    abstract fun message(): String

}

