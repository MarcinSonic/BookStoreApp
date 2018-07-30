package pl.marcingorski.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * API Contract for the Pets app.
 */

public final class BooksContract {

    private BooksContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "pl.marcingorski.bookstoreapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */

    public static final Uri BASE_CONTENT_URI = Uri.parse ( "content://" + CONTENT_AUTHORITY );

    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single pet.
     */

    public static final class BooksEntry implements BaseColumns {

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** Name of database table for books */
        public final static  String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOKS_NAME = "name";
        public final static String COLUMN_BOOKS_PRICE = "price";
        public final static String COLUMN_BOOKS_QUANTITY = "quantity";
        public final static String COLUMN_BOOKS_SUP_NAME = "sup_name";
        public final static String COLUMN_BOOKS_SUP_PHONE = "sup_phone";

    }
}