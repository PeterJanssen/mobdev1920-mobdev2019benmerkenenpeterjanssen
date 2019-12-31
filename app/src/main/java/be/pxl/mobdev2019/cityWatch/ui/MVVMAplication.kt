package be.pxl.mobdev2019.cityWatch.ui

import android.app.Application
import be.pxl.mobdev2019.cityWatch.data.repositories.FireBaseRepository
import be.pxl.mobdev2019.cityWatch.data.repositories.ReportRepository
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModelFactory
import be.pxl.mobdev2019.cityWatch.ui.list.ListViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

// MVVMApplication is registered in androidmanifest as an application
class MVVMApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { FireBaseRepository() }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { ReportRepository(instance()) }

        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { ListViewModelFactory(instance()) }

    }

}