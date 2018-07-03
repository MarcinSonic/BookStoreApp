package pl.marcingorski.bookstoreapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pl.marcingorski.bookstoreapp.data.BooksDbHelper;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    /** EditText field to enter the book's name */
    private EditText mNameEditText;

    /** EditText field to enter the books's price */
    private EditText mPriceEditText;

    /** EditText field to enter the book's quantity */
    private EditText mQuantitytEditText;

    /** EditText field to enter the books's suplier name */
    private EditText mSupNameEditText;

    /** EditText field to enter the books's suplier phone */
    private EditText mSupPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantitytEditText = (EditText) findViewById(R.id.edit_book_quantity);
        mSupNameEditText = (EditText) findViewById(R.id.edit_book_sup_name);
        mSupPhoneEditText = (EditText) findViewById(R.id.edit_book_sup_phone);

    }

    private void insertBook() {

        String nameString = mNameEditText.getText ().toString ().trim ();
        String priceString = mPriceEditText.getText ().toString ().trim ();
        String quantityString = mQuantitytEditText.getText ().toString ().trim ();
        int quantity = Integer.parseInt ( quantityString );
        String supNameString = mSupNameEditText.getText ().toString ().trim ();
        String supPhoneString = mSupPhoneEditText.getText ().toString ().trim ();

        BooksDbHelper mDbHelper = new BooksDbHelper ( this );

        SQLiteDatabase db = mDbHelper.getWritableDatabase ();

        ContentValues values = new ContentValues (  );
        values.put ( BooksEntry.COLUMN_BOOKS_NAME, nameString );
        values.put ( BooksEntry.COLUMN_BOOKS_PRICE, priceString );
        values.put ( BooksEntry.COLUMN_BOOKS_QUANTITY, quantity );
        values.put ( BooksEntry.COLUMN_BOOKS_SUP_NAME, supNameString );
        values.put ( BooksEntry.COLUMN_BOOKS_SUP_PHONE, supPhoneString );


        long newRowId = db.insert ( BooksEntry.TABLE_NAME, null,values );

        if (newRowId == -1) {
            Toast.makeText ( this, "Error with saving book", Toast.LENGTH_SHORT ).show ();

        } else {
            Toast.makeText ( this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT ).show ();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertBook ();
                finish ();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
