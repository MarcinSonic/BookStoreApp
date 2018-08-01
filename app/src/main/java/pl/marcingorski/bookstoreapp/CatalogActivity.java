package pl.marcingorski.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import pl.marcingorski.bookstoreapp.data.BooksDbHelper;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup Book to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the books data
        ListView bookListView = (ListView) findViewById ( R.id.list );

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter ( this, null );
        bookListView.setAdapter ( mCursorAdapter );

        bookListView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {

                Intent intent = new Intent ( CatalogActivity.this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId ( BooksEntry.CONTENT_URI, id );

                intent.setData ( currentBookUri );
                startActivity ( intent );

            }
        } );

        getLoaderManager ().initLoader ( BOOK_LOADER, null, this );
    }

    private void insertBook() {

        ContentValues values = new ContentValues();

        values.put ( BooksEntry.COLUMN_BOOKS_NAME, "Isola" );
        values.put ( BooksEntry.COLUMN_BOOKS_PRICE, "1.99" );
        values.put ( BooksEntry.COLUMN_BOOKS_QUANTITY, 1);
        values.put ( BooksEntry.COLUMN_BOOKS_SUP_NAME, "SuperNova" );
        values.put ( BooksEntry.COLUMN_BOOKS_SUP_PHONE, "666 666 666" );

        Uri newUri = getContentResolver ().insert(BooksEntry.CONTENT_URI, values);

    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver ().delete ( BooksEntry.CONTENT_URI, null, null );
        Log.v ( "Catalog Activity", rowsDeleted + " rows deleted from books database" );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllBooks ();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_BOOKS_NAME,
                BooksEntry.COLUMN_BOOKS_PRICE,
                BooksEntry.COLUMN_BOOKS_QUANTITY
        };
        return new CursorLoader ( this, BooksEntry.CONTENT_URI,
                projection,
                null,null,null);
    }

    @Override
    public void onLoadFinished(Loader <Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader <Cursor> loader) {
        mCursorAdapter.swapCursor ( null );

    }
}