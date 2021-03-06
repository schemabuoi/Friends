package com.example.friends.model.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.friends.entity.Friend;
import com.example.friends.model.database.converter.Converters;

import java.util.Date;
import java.util.GregorianCalendar;

@Database(entities = {Friend.class}, version = FriendsDatabase.VERSION)
@TypeConverters({Converters.class})
public abstract class FriendsDatabase extends RoomDatabase {

    public static final int VERSION = 1;

    private static FriendsDatabase instance;

    public abstract FriendDao friendDao();

    public static synchronized FriendsDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FriendsDatabase.class, "friends_database").
                    fallbackToDestructiveMigration().
                    addCallback(populateCallback).
                    build();
        }
        return instance;
    }

    private static RoomDatabase.Callback populateCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateTask(instance).execute();
        }
    };

    private static class PopulateTask extends AsyncTask<Void, Void, Void> {

        private FriendDao friendDao;

        private PopulateTask(FriendsDatabase db)
        {
            friendDao = db.friendDao();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            friendDao.insert(new Friend(
                    "Grzegorz Charyszczak",
                    "+48 502 288 995",
                     new GregorianCalendar(1998, 8, 16).getTime(),
                    "schemabuoi@gmail.com",
                    "Popowicka 146/27 Wroclaw",
                    "www.schemabuoi.github.io/grzegorz-charyszczak/",
                    true,
                    ""));

            friendDao.insert(new Friend(
                    "Oliwia Skrzypaszek",
                    "+48 690 421 069",
                     new GregorianCalendar(2000, 5, 24).getTime(),
                    "oliwiaskrzypaszek@gmail.com",
                    "Piotra Stachiewicza 15/20 Krakow",
                    "www.skrzypaszek.pl",
                    true,
                    ""));

            friendDao.insert(new Friend(
                    "Marcin Dwornikowski",
                    "+48 607 412 984",
                    new GregorianCalendar(1998, 4, 24).getTime(),
                    "mdwornikowski@gmail.com",
                    "",
                    "www.mdwornikowski.com/",
                    true,
                    ""));

            friendDao.insert(new Friend(
                    "Iga Molendowska",
                    "+48 883 736 886",
                    new GregorianCalendar(1998, 3, 24).getTime(),
                    "igamolendowska@gmail.com",
                    "",
                    "",
                    true,
                    ""));

            friendDao.insert(new Friend(
                    "David Kalatzis",
                    "+45 81 90 84 96",
                    null,
                    "davidkalatzis@gmail.com",
                    "",
                    "www.kidagine.github.io/davidkalatzis/",
                    true,
                    ""));

            friendDao.insert(new Friend(
                    "Mate Kiss",
                    "+45 50 32 43 85",
                    new GregorianCalendar(1997, 3, 24).getTime(),
                    "matekiss@gmail.com",
                    "",
                    "",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Nedas Surkus",
                    "+45 16 78 34 72",
                    null,
                    "nedassurkus@gmail.com",
                    "",
                    "",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Marek Stancik",
                    "+45 74 67 83 12",
                    null,
                    "marekstancik@gmail.com",
                    "",
                    "",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Jan Toth",
                    "+45 66 40 15 91",
                    null,
                    "jantoth@gmail.com",
                    "",
                    "",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Alex Pedersen",
                    "+45 40 31 15 20",
                     new GregorianCalendar(1996, 6, 7).getTime(),
                    "alexpedersen@gmail.com",
                    "",
                    "",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Kuba Rewald",
                    "+45 72 21 14 15",
                     new GregorianCalendar(1999, 2, 14).getTime(),
                    "kubarewald@gmail.com",
                    "",
                    "www.github.com/RebelStoke",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Radoslaw Haller",
                    "+45 40 20 76 91",
                     new GregorianCalendar(2000, 0, 2).getTime(),
                    "radoslawhaller@gmail.com",
                    "",
                    "www.github.com/darthradek",
                    false,
                    ""));

            friendDao.insert(new Friend(
                    "Adi Tutor",
                    "+45 59 91 15 20",
                     new GregorianCalendar(2000, 3, 24).getTime(),
                    "aditutor@gmail.com",
                    "",
                    "",
                    false,
                    ""));

            return null;
        }
    }
}
