rootProject.name = "java-util"
include("common")
include("math")
include("reflector")
include("serialization")
include("expression")
include("maven")

plugins {
    id("com.gradleup.nmcp.settings").version("1.4.0")
}

nmcpSettings {
    centralPortal {
        username = System.getenv("CENTRAL_USERNAME")
        password = System.getenv("CENTRAL_PASSWORD")
        publishingType = "USER_MANAGED"
    }
}
