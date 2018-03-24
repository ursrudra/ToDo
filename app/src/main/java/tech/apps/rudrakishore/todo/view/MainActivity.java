package tech.apps.rudrakishore.todo.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tech.apps.rudrakishore.todo.R;
import tech.apps.rudrakishore.todo.database.DatabaseHelper;
import tech.apps.rudrakishore.todo.database.model.Item;
import tech.apps.rudrakishore.todo.util.MyDividerItemDecoration;
import tech.apps.rudrakishore.todo.util.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static String selectedItem;
    String[] fruits = {"Ayush cream", "Karjuram", "Jeedi papu", "Sugar", "Rin Pack", "Rin soaps", "Exo Dishwash", "Dabur amla oil", "Honey", "Pesrapappu", "Thokka Pesarapappu", "Verusenaga", "Bru Coffee", "3roses Tea", "Lux Soap", "Idli nuka", "Ors", "All out", "Vammu", "Saggu biyyam"};
    private ItemsAdapter itemsAdapter;
    private List<Item> itemList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;
    private ImageView noNotesImg;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.checklist);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesImg = findViewById(R.id.rest);
        noNotesView = findViewById(R.id.empty_notes_view);


        db = new DatabaseHelper(this);

        itemList.addAll(db.getAllItems());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        itemsAdapter = new ItemsAdapter(this, itemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(itemsAdapter);

        toggleEmptyNotes();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                updateStatus(position);
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createItem(String note, double qty, String metric) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertItem(note, qty, metric);

        // get the newly inserted note from db
        Item n = db.getItem(id);

        if (n != null) {
            // adding new note to array list at 0 position
            itemList.add(0, n);

            // refreshing the list
            itemsAdapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateItem(String note, double qty, String metric, int position) {
        Item n = itemList.get(position);
        // updating note text
        n.setItem(note);
        n.setQty(qty);
        n.setMetric(metric);

        // updating note in db
        db.updateItem(n);

        // refreshing the list
        itemList.set(position, n);
        itemsAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateStatus(int position) {
        Item n = itemList.get(position);
        // updating note text
        if (n.getStatus() == 0) {
            n.setStatus(1);

        } else if (n.getStatus() == 1) {
            n.setStatus(0);
        }
        // updating note in db
        db.updateItem(n);

        // refreshing the list
        itemList.set(position, n);
        itemsAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteItem(int position) {
        // deleting the note from db
        db.deleteItem(itemList.get(position));

        // removing the note from the list
        itemList.remove(position);
        itemsAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
        itemsAdapter.notifyDataSetChanged();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, itemList.get(position), position);
                } else {
                    deleteItem(position);
                }
            }
        });
        builder.show();
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Item item, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.item_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final AutoCompleteTextView inputNote = view.findViewById(R.id.item);
        final EditText inputQty = view.findViewById(R.id.qty);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.spinner_item, fruits);
        //Getting the instance of AutoCompleteTextView
        inputNote.setThreshold(1);//will start working from first character
        inputNote.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
//        inputNote.setTextColor(Color.RED);

        Spinner qtySpinner = view.findViewById(R.id.spinnerQty);
        qtySpinner.setOnItemSelectedListener(this);
        List<String> qtyList = new ArrayList<String>();
        qtyList.add("Kgs");
        qtyList.add("grms");
        qtyList.add("Dozen");
        qtyList.add("Packs");
        qtyList.add("Items");
        qtyList.add("Rupees");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,qtyList);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, qtyList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        // Drop down layout style - list view with radio button
        qtySpinner.setAdapter(spinnerArrayAdapter);

        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && item != null) {
            inputNote.setText(item.getItem());
            inputQty.setText(item.getQty() + "");
            qtySpinner.setSelection(qtyList.indexOf(item.getMetric()), true);
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && item != null) {
                    // update note by it's id
                    updateItem(inputNote.getText().toString(), Double.parseDouble(inputQty.getText().toString()), selectedItem, position);
                } else {
                    // create new note
                    createItem(inputNote.getText().toString(), Double.parseDouble(inputQty.getText().toString()), selectedItem);
                }
            }
        });
    }


    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getItemsCount() > 0) {
            noNotesView.setVisibility(View.GONE);
            noNotesImg.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
            noNotesImg.setVisibility(View.VISIBLE);

        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = parent.getItemAtPosition(position).toString();

        selectedItem = item;
        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
