package olvb.reyalp

class Application: android.app.Application()  {
    // used to easily retrieve application context from anywhere (ie. view models)
    companion object {
        lateinit var instance: Application
            private set
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}