package pl.marcingorski.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;

/**
 * Database helper for Books app. Manages database creation and version management.
 */
public class BooksDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BooksDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "books.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BooksDbHelper}.
     *
     * @param context of the app
     */
    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + BooksEntry.TABLE_NAME + " ("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_BOOKS_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_BOOKS_PRICE + " TEXT, "
                + BooksEntry.COLUMN_BOOKS_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_BOOKS_SUP_NAME + " TEXT, "
                + BooksEntry.COLUMN_BOOKS_SUP_PHONE + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}