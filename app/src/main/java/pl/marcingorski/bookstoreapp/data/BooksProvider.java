package pl.marcingorski.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;
import java.security.Provider;

/**
 * {@link ContentProvider} for Books app.
 */
public class BooksProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = BooksProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the books table */
    private static final int BOOKS = 100;

    /** URI matcher code for the content URI for a single book in the books table */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS, BOOKS);

        sUriMatcher.addURI(BooksContract.CONTENT_AUTHORITY, BooksContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /** Database helper object */
    private BooksDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:

                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BooksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri ( getContext ().getContentResolver (),uri );

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BooksEntry.COLUMN_BOOKS_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a title");
        }

        // If the quantity is provided, check that it's greater than or equal to 0
        Integer quantity = values.getAsInteger(BooksEntry.COLUMN_BOOKS_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Book requires valid quantity");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BooksEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext ().getContentResolver ().notifyChange ( uri, null );
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BooksEntry.COLUMN_BOOKS_NAME)) {
            String name = values.getAsString(BooksEntry.COLUMN_BOOKS_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }

        if (values.containsKey(BooksEntry.COLUMN_BOOKS_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(BooksEntry.COLUMN_BOOKS_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update ( BooksEntry.TABLE_NAME, values, selection, selectionArgs );

        if (rowsUpdated != 0) {
            getContext ().getContentResolver ().notifyChange ( uri, null );
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete ( BooksEntry.TABLE_NAME, selection, selectionArgs );
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BooksEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BooksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext ().getContentResolver ().notifyChange ( uri, null );
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BooksEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BooksEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
