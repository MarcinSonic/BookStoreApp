package pl.marcingorski.bookstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import pl.marcingorski.bookstoreapp.data.BooksDbHelper;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private BooksDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup Book to open EditorActivity
        FloatingActionButton book = (FloatingActionButton) findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new BooksDbHelper ( this );

        displayDatabaseInfo();
    }

    @Override
    protected  void  onStart() {
        super.onStart ();
        displayDatabaseInfo ();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_BOOKS_NAME,
                BooksEntry.COLUMN_BOOKS_PRICE,
                BooksEntry.COLUMN_BOOKS_QUANTITY,
                BooksEntry.COLUMN_BOOKS_SUP_NAME,
                BooksEntry.COLUMN_BOOKS_SUP_PHONE
        };

        Cursor cursor = db.query (
                BooksEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = (TextView) findViewById(R.id.text_view_book);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // books table in the database).

            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(BooksEntry._ID + " - " +
                    BooksEntry.COLUMN_BOOKS_NAME + " - " +
                    BooksEntry.COLUMN_BOOKS_PRICE + " - " +
                    BooksEntry.COLUMN_BOOKS_QUANTITY + " - " +
                    BooksEntry.COLUMN_BOOKS_SUP_NAME + " - " +
                    BooksEntry.COLUMN_BOOKS_SUP_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BooksEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOKS_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOKS_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOKS_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOKS_SUP_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(BooksEntry.COLUMN_BOOKS_SUP_PHONE);

            // Iterate through all the returned rows in the cursor

            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupName = cursor.getString(supNameColumnIndex);
                String currentSupPhone = cursor.getString(supPhoneColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupName + " - " +
                        currentSupPhone));}
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertBook() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put ( BooksEntry.COLUMN_BOOKS_NAME, "Isola" );
        values.put ( BooksEntry.COLUMN_BOOKS_PRICE, "1.99" );
        values.put ( BooksEntry.COLUMN_BOOKS_QUANTITY, 1);
        values.put ( BooksEntry.COLUMN_BOOKS_SUP_NAME, "SuperNova" );
        values.put ( BooksEntry.COLUMN_BOOKS_SUP_PHONE, "666 666 666" );

        long newRowId = db.insert ( BooksEntry.TABLE_NAME, null, values );
        Log.v("CatalogActivity", "New row ID" + newRowId);

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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}