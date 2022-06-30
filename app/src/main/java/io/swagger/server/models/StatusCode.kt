package io.swagger.server.models

/**
 * The status code.
 */
enum class StatusCode(val value: Int) {
    NOT_IMPLEMENTED(1000) {
        override fun message(extraMessage: String?): String {
            TODO("not implemented" + addExtraMessage(extraMessage))
        }
    },

    TIMEOUT_WINDOW_UPDATE(1001) {
        override fun message(extraMessage: String?): String {
            TODO("not implemented")
        }
    },

    ARGUMENT_MISSING(3001) {

        override fun message(extraMessage: String?): String {
            TODO("not implemented")
        }
    },

    INTERACTION_KEY(5001) {

        override fun message(extraMessage: String?): String {
            TODO("not implemented")
        }
    },

    OBJECT_NOT_FOUND(6000) {

        override fun message(extraMessage: String?): String {
            return "Object not found" + addExtraMessage(extraMessage)
        }
    };

    fun addExtraMessage(extraMessage: String?): String {
        return (if (extraMessage != null) ": " else "") +
                (extraMessage ?: "")
    }

    abstract fun message(extraMessage: String? = null): String

}

