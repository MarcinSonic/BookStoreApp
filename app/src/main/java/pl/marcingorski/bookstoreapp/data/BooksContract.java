package pl.marcingorski.bookstoreapp.data;

import android.provider.BaseColumns;

public final class BooksContract {

    private BooksContract() {}

    public static final class BooksEntry implements BaseColumns {

        public final static  String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOKS_NAME = "name";
        public final static String COLUMN_BOOKS_PRICE = "price";
        public final static String COLUMN_BOOKS_QUANTITY = "quantity";
        public final static String COLUMN_BOOKS_SUP_NAME = "sup_name";
        public final static String COLUMN_BOOKS_SUP_PHONE = "sup_phone";

    }
}