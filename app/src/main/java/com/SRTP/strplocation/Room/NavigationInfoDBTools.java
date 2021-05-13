package com.SRTP.strplocation.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NavigationInfoDBTools extends AndroidViewModel {
    private NavigationInfoDao navigationInfoDao;
    private LiveData<List<NavigationMessageForDB>> allNavigationInfos;
    public NavigationInfoDBTools(@NonNull Application application) {
        super(application);
        NavigationInfoDB navigationInfoDB=NavigationInfoDB.getDataBase(application);
        navigationInfoDao=navigationInfoDB.getNavigationInfoDao();
        allNavigationInfos=navigationInfoDao.getAllNavigationInfos();
    }

    public void insertNavigationInfo(NavigationMessageForDB... navigationMessageForDB){
        new InsertAsyncTask(navigationInfoDao).execute(navigationMessageForDB);
    }
    public void updateNavigationInfo(NavigationMessageForDB... NavigationMessageForDB){
        new UpdateAsyncTask(navigationInfoDao).execute(NavigationMessageForDB);
    }
    public void deleteNavigationInfo(NavigationMessageForDB... NavigationMessageForDB){
        new DeleteAsyncTask(navigationInfoDao).execute(NavigationMessageForDB);
    }
    public void deleteAll(){
        new DeleteAllAsyncTask(navigationInfoDao).execute();
    }

    public LiveData<List<NavigationMessageForDB>> getAllNavigationInfoLD() {
        return allNavigationInfos;
    }

    static class InsertAsyncTask extends AsyncTask<NavigationMessageForDB,Void,Void> {
        private NavigationInfoDao navigationInfoDao;

        public InsertAsyncTask(NavigationInfoDao NavigationInfoDao) {
            this.navigationInfoDao = NavigationInfoDao;
        }

        @Override
        protected Void doInBackground(NavigationMessageForDB... NavigationMessageForDB) {
            navigationInfoDao.InsertNavigationInfos(NavigationMessageForDB);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<NavigationMessageForDB,Void,Void>{
        private NavigationInfoDao navigationInfoDao;

        public UpdateAsyncTask(NavigationInfoDao NavigationInfoDao) {
            this.navigationInfoDao = NavigationInfoDao;
        }

        @Override
        protected Void doInBackground(NavigationMessageForDB... NavigationMessageForDB) {
            navigationInfoDao.UpdateNavigationInfos(NavigationMessageForDB);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<NavigationMessageForDB,Void,Void>{
        private NavigationInfoDao navigationInfoDao;

        public DeleteAsyncTask(NavigationInfoDao NavigationInfoDao) {
            this.navigationInfoDao = NavigationInfoDao;
        }

        @Override
        protected Void doInBackground(NavigationMessageForDB... NavigationMessageForDB) {
            navigationInfoDao.DeleteNavigationInfos(NavigationMessageForDB);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private NavigationInfoDao navigationInfoDao;

        public DeleteAllAsyncTask(NavigationInfoDao NavigationInfoDao) {
            this.navigationInfoDao = NavigationInfoDao;
        }

        @Override
        protected Void doInBackground(Void...voids) {
            navigationInfoDao.DeleteAll();
            return null;
        }
    }
}
