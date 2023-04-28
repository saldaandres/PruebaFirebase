package com.example.pruebafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    EditText editPetName;
    EditText editOwner;
    EditText editAge;
    EditText editSpecies;
    Button btnAdd;
    Button btnEdit;
    Button btnSearch;
    Mascota mascota = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        editAge = findViewById(R.id.editTextPetAge);
        editPetName = findViewById(R.id.editTextPetName);
        editSpecies = findViewById(R.id.editTextPetSpecies);
        editOwner = findViewById(R.id.editTextOwnerName);
        btnEdit = findViewById(R.id.buttonEdit);
        btnAdd = findViewById(R.id.buttonAdd);
        btnSearch = findViewById(R.id.buttonSearch);
        btnEdit.setEnabled(false);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String petName = editPetName.getText().toString();
                String ownerName = editOwner.getText().toString();
                String species = editSpecies.getText().toString();
                String stringAge = editAge.getText().toString();
                int age;

                if (petName.isEmpty() || ownerName.isEmpty() || species.isEmpty() || stringAge.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                age = Integer.parseInt(stringAge);
                Mascota mascota = new Mascota(petName, species, age, ownerName);
                tryAddingPet(mascota);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPetName.getText().toString().isEmpty() || editOwner.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Debes rellenar los campos Nombre Mascota y Nombre Dueño para buscar", Toast.LENGTH_LONG).show();
                    return;
                }
                String petName = editPetName.getText().toString();
                String ownerName = editOwner.getText().toString();
                findPet(petName, ownerName);
            }
        });
    }

    private void addPet(Mascota mascota) {
        database.collection("mascotas")
                .add(mascota)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Mascota agregada con id: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findPet(String petName, String ownerName) {
        database.collection("mascotas")
                .whereEqualTo("owner", ownerName)
                .whereEqualTo("petName", petName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                mascota = null;
                                editAge.setText("");
                                editSpecies.setText("");
                                Toast.makeText(MainActivity.this, "Esta mascota no existe", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mascota = document.toObject(Mascota.class);
                            }
                            rellenarDatos(mascota);
                        }
                    }
                });
    }

    private void tryAddingPet(Mascota mascota) {
        final Mascota nuevaMascota = mascota;
        database.collection("mascotas")
                .whereEqualTo("owner", mascota.getOwner())
                .whereEqualTo("petName", mascota.getPetName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                               addPet(nuevaMascota);
                               return;
                            }
                            Toast.makeText(MainActivity.this, "Esta mascota ya existe en la BD", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void rellenarDatos(Mascota mascota) {
        editAge.setText(String.valueOf(mascota.getAge()));
        editSpecies.setText(mascota.getSpecies());
        btnEdit.setEnabled(true);
    }
}