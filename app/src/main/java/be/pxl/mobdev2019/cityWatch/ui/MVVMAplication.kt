package be.pxl.mobdev2019.cityWatch.ui

import android.app.Application
import be.pxl.mobdev2019.cityWatch.data.repositories.FireBaseRepository
import be.pxl.mobdev2019.cityWatch.data.repositories.ReportRepository
import be.pxl.mobdev2019.cityWatch.data.repositories.UserRepository
import be.pxl.mobdev2019.cityWatch.ui.account.AccountViewModel
import be.pxl.mobdev2019.cityWatch.ui.account.AccountViewModelFactory
import be.pxl.mobdev2019.cityWatch.ui.auth.AuthViewModelFactory
import be.pxl.mobdev2019.cityWatch.ui.create_report.CreateReportViewModel
import be.pxl.mobdev2019.cityWatch.ui.create_report.CreateReportViewModelFactory
import be.pxl.mobdev2019.cityWatch.ui.detail_report.ReportViewModel
import be.pxl.mobdev2019.cityWatch.ui.detail_report.ReportViewModelFactory
import be.pxl.mobdev2019.cityWatch.ui.list_report.AllReportsViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


// MVVMApplication is registered in androidmanifest as an application
class MVVMApplication : Application(), KodeinAware {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val mDatabaseReference = FirebaseDatabase.getInstance()
        mDatabaseReference.setPersistenceEnabled(true)
        val mUserDataBaseReference = mDatabaseReference.getReference("Users")
        val mReportDataBaseReference = mDatabaseReference.getReference("Reports")
        mUserDataBaseReference.keepSynced(true)
        mReportDataBaseReference.keepSynced(true)
    }

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { FireBaseRepository() }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { ReportRepository(instance()) }

        bind() from provider { AuthViewModelFactory(instance()) }
        bind() from provider { AllReportsViewModelFactory(instance()) }

        bind() from provider { CreateReportViewModel(instance()) }
        bind() from provider { CreateReportViewModelFactory(instance()) }

        bind() from provider { AccountViewModel(instance()) }
        bind() from provider { AccountViewModelFactory(instance()) }

        bind() from provider { ReportViewModel(instance()) }
        bind() from provider { ReportViewModelFactory(instance()) }
    }

}