package pl.marcingorski.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing books (null if it's a new book)
     */

    private Uri mCurrentBookUri;

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

    private  boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener () {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent ();
        mCurrentBookUri = intent.getData ();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle ( getString ( R.string.editor_activity_title_new_book ) );

            invalidateOptionsMenu ();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle ( getString ( R.string.editor_activity_title_edit_book ) );

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager ().initLoader ( EXISTING_BOOK_LOADER, null, this );
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantitytEditText = findViewById(R.id.edit_book_quantity);
        mSupNameEditText = findViewById(R.id.edit_book_sup_name);
        mSupPhoneEditText = findViewById(R.id.edit_book_sup_phone);

        mNameEditText.setOnTouchListener ( mTouchListener );
        mPriceEditText.setOnTouchListener ( mTouchListener );
        mQuantitytEditText.setOnTouchListener ( mTouchListener );
        mSupNameEditText.setOnTouchListener ( mTouchListener );
        mSupPhoneEditText.setOnTouchListener ( mTouchListener );


    }

    /**
     * Get user input from editor and save book into database.
     */

    private void saveBook() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText ().toString ().trim ();
        String priceString = mPriceEditText.getText ().toString ().trim ();
        String quantityString = mQuantitytEditText.getText ().toString ().trim ();
        String supNameString = mSupNameEditText.getText ().toString ().trim ();
        String supPhoneString = mSupPhoneEditText.getText ().toString ().trim ();

        if (mCurrentBookUri == null && TextUtils.isEmpty ( nameString ) && TextUtils.isEmpty ( priceString ) && TextUtils.isEmpty ( quantityString )) {
            finish ();}

        if (TextUtils.isEmpty ( nameString ) || TextUtils.isEmpty ( priceString ) || TextUtils.isEmpty ( quantityString )) {
            Toast.makeText ( this, "Fill empty fields", Toast.LENGTH_SHORT ).show ();

        } else {


            // Create a ContentValues object where column names are the keys,
            // and book attributes from the editor are the values.

            ContentValues values = new ContentValues ();
            values.put ( BooksEntry.COLUMN_BOOKS_NAME, nameString );
            values.put ( BooksEntry.COLUMN_BOOKS_PRICE, priceString );
            int quantity = 0;
            if (!TextUtils.isEmpty ( quantityString )) {
                quantity = Integer.parseInt ( quantityString );
            }
            values.put ( BooksEntry.COLUMN_BOOKS_QUANTITY, quantity );
            values.put ( BooksEntry.COLUMN_BOOKS_SUP_NAME, supNameString );
            values.put ( BooksEntry.COLUMN_BOOKS_SUP_PHONE, supPhoneString );


            // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
            if (mCurrentBookUri == null) {
                // This is a NEW book, so insert a new book into the provider,
                // returning the content URI for the new book.
                Uri newUri = getContentResolver ().insert ( BooksEntry.CONTENT_URI, values );

                // Show a toast message depending on whether or not the insertion was successful.

                if (newUri == null) {
                    Toast.makeText ( this, "Error with saving book", Toast.LENGTH_SHORT ).show ();

                } else {
                    Toast.makeText ( this, "Book saved", Toast.LENGTH_SHORT ).show ();
                }

            } else {
                // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentBookUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver ().update ( mCurrentBookUri, values, null, null );

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText ( this, getString ( R.string.editor_update_book_failed ),
                            Toast.LENGTH_SHORT ).show ();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText ( this, getString ( R.string.editor_update_book_successful ),
                            Toast.LENGTH_SHORT ).show ();
                }
            }
            finish ();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu ( menu );
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem ( R.id.action_delete );
            menuItem.setVisible ( false );
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveBook ();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog ();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {

                    NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                    }
                };
                showUnsavedChangesDialog ( discardButtonClickListener );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed ();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ();
            }
        };
        showUnsavedChangesDialog ( discardButtonClickListener );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the books table
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_BOOKS_NAME,
                BooksEntry.COLUMN_BOOKS_PRICE,
                BooksEntry.COLUMN_BOOKS_QUANTITY,
                BooksEntry.COLUMN_BOOKS_SUP_NAME,
                BooksEntry.COLUMN_BOOKS_SUP_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader ( this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null );                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader <Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount () < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst ()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_NAME );
            int priceColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_QUANTITY );
            int supNameColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_SUP_NAME );
            int supPhoneColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_SUP_PHONE );


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString ( nameColumnIndex );
            String price = cursor.getString ( priceColumnIndex );
            int quantity = cursor.getInt ( quantityColumnIndex );
            String supName = cursor.getString ( supNameColumnIndex );
            String supPhone = cursor.getString ( supPhoneColumnIndex );


            // Update the views on the screen with the values from the database
            mNameEditText.setText ( name );
            mPriceEditText.setText ( price );
            mQuantitytEditText.setText ( Integer.toString ( quantity ) );
            mSupNameEditText.setText ( supName );
            mSupPhoneEditText.setText ( supPhone );
        }
    }

    @Override
    public void onLoaderReset(Loader <Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText ( "" );
        mPriceEditText.setText ( "" );
        mQuantitytEditText.setText ( "" );
        mSupNameEditText.setText ( "" );
        mSupPhoneEditText.setText ( "" );
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.unsaved_changes_dialog_msg );
        builder.setPositiveButton ( R.string.discard, discardButtonClickListener );
        builder.setNegativeButton ( R.string.keep_editing, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );

        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.delete_dialog_msg );
        builder.setPositiveButton ( R.string.delete, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook ();
            }
        } );

        builder.setNegativeButton ( R.string.cancel, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );

        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();

    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver ().delete ( mCurrentBookUri, null, null );

            if (rowsDeleted == 0) {
                Toast.makeText ( this, getString ( R.string.editor_delete_book_failed ),
                        Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.editor_delete_book_successful ), Toast.LENGTH_SHORT ).show ();
            }
        }
        finish ();
    }

}
