/*
Класс: ApplicationUserActivity
Язык: Java
Краткое описание:
Класс отображаюзий заявку пользователя на заселение в общежитие.

Глобальные переменные:
    userId - переменная для получения id пользователя подающего заявку;
    db - подключение к базе данных комнат;
    userDB - подключение к базе данных пользователей;
    userRefDB - ссылка на базу данных пользователей;
    applicationDB - подключение к базе данных заявок.

Функции, используемая в форме:
    onCreate – функция для вызова формы "activty_application_user";
    onClickExit – функция для закрытия текущего окна;
    onClickYes - функция для принятия заявки на заселение;
    onClickNo - функция для отклонения заявки на заселение.
*/
package com.hfad.mydiplomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hfad.mydiplomapp.classApp.Application;
import com.hfad.mydiplomapp.classApp.User;
import com.squareup.picasso.Picasso;

public class ApplicationUserActivity extends AppCompatActivity {
    private String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase userDB;
    private DatabaseReference userRefDB;
    private DatabaseReference applicationDB = FirebaseDatabase.getInstance().getReference("Application");

    /*
    onCreate - функция для запуска формы "activty_application_user".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Глобальные переменные:
        userId - переменная для получения id пользователя подающего заявку;
        userDB - подключение к базе данных пользователей;
        userRefDB - ссылка на базу данных пользователей.
    Локальные переменные:
        intent - для получения данных с прошлого окна "activty_application_consider";
        lastName - отображение фамилии в поданной заявке;
        name - отображение имени в поданной заявке;
        sureName - отображение отчества в поданной заявке;
        bithday - отображение даты рождения в поданной заявке;
        gender - отображение полового признака в поданной заявке;
        imBD - отображение фотографии в поданной заявке.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_user);

        Intent intent = getIntent();

        userDB  = FirebaseDatabase.getInstance();
        userRefDB = userDB.getReference("User");
        //Получение данных из прошлого окна со списком заявок и их отображение
        userId = intent.getStringExtra("applicationUserUID");
        TextView lastName = findViewById(R.id.text_application_lastName);
        TextView name = findViewById(R.id.text_application_name);
        TextView sureName = findViewById(R.id.text_application_sureName);
        TextView bithday = findViewById(R.id.text_application_bithday);
        TextView gender = findViewById(R.id.text_application_gender);
        ImageView imBD = findViewById(R.id.imageViewApplicationUser);

        lastName.setText(intent.getStringExtra("lastName"));
        name.setText(intent.getStringExtra("name"));
        sureName.setText(intent.getStringExtra("sureName"));
        bithday.setText(intent.getStringExtra("bithday"));
        gender.setText(intent.getStringExtra("gender"));
        Picasso.get().load(intent.getStringExtra("imageId")).into(imBD);
    }

    /*
    onClickExit – функция для закрытия текущего окна.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    */
    public void onClickExit(View view) {
        finish();
    }

    /*
    onClickYes - функция для принятия заявки на заселение.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        db - подключение к базе данных комнат;
        applicationDB - подключение к базе данных заявок;
        userId - переменная для получения id пользователя подающего заявку;
        userRefDB - ссылка на базу данных пользователей.
    Локальные переменные:
        roomsCollectionRef - получение ссылки для подключения к базе данных комнат;
        genderView - получение полового признака заявки;
        gender - преобразование genderView в текст;
        intent - переменная для перехода в окно "activity_application_consider";
        firstDocument - первый объект комнаты подходящий по условию;
        numberRoom - переменная с номером комнаты;
        application - получение данных из БД заявок;
        user - получение данных из БД пользователей;
        valueEventListener - слушатель для получения данных из БД заявок;
        valueEventListener1 - слушатель для получения данных из БД пользователей.
    */
    public void onClickYes(View view) {
        //Получение ссылки базы данных комнат
        CollectionReference roomsCollectionRef = db.collection("rooms");
        TextView genderView = findViewById(R.id.text_application_gender);
        String gender = genderView.getText().toString();

        Intent intent = new Intent(this, ApplicationConsiderActivity.class);
        startActivity(intent);
        //Запрос на получение пустых блоков по половому признаку
        roomsCollectionRef.whereEqualTo("gender", gender)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() == 0) {
                        //Сообщение о том, что свободных мест нет
                        Toast.makeText(getApplicationContext(),"Невозможно заселить данного пользователя, возможно нет мест.", Toast.LENGTH_SHORT).show();
                    } else {
                        QueryDocumentSnapshot firstDocument = queryDocumentSnapshots.getDocumentChanges().get(0).getDocument();
                        String numberRoom = firstDocument.getId();
                        if (firstDocument.getString("3_1").equals("✖")) {
                            roomsCollectionRef.document(numberRoom).update("3_1", userId);
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Application application = ds.getValue(Application.class);
                                        if (application.getUserId().equals(userId)) {
                                            //Изменение стадии заявки после ее принятия
                                            ds.getRef().child("applicationStage").setValue(2);
                                            applicationDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            ValueEventListener valueEventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getUserId().equals(userId)) {
                                            //Добавление номера комнаты в профиль пользователя
                                            ds.getRef().child("room").setValue(numberRoom);
                                            userRefDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            userRefDB.addListenerForSingleValueEvent(valueEventListener1);
                            applicationDB.addListenerForSingleValueEvent(valueEventListener);
                            Toast.makeText(getApplicationContext(),"Заселен(а) в " + numberRoom, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();

                        } else if (firstDocument.getString("3_2").equals("✖")) {
                            roomsCollectionRef.document(numberRoom).update("3_2", userId);
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Application application = ds.getValue(Application.class);
                                        if (application.getUserId().equals(userId)) {
                                            //Изменение стадии заявки после ее принятия
                                            ds.getRef().child("applicationStage").setValue(2);
                                            applicationDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            ValueEventListener valueEventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getUserId().equals(userId)) {
                                            //Добавление номера комнаты в профиль пользователя
                                            ds.getRef().child("room").setValue(numberRoom);
                                            userRefDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            userRefDB.addListenerForSingleValueEvent(valueEventListener1);
                            applicationDB.addListenerForSingleValueEvent(valueEventListener);
                            Toast.makeText(getApplicationContext(),"Заселен(а) в " + numberRoom, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();

                        } else if (firstDocument.getString("3_3").equals("✖")) {
                            roomsCollectionRef.document(numberRoom).update("3_3", userId);
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Application application = ds.getValue(Application.class);
                                        if (application.getUserId().equals(userId)) {
                                            //Изменение стадии заявки после ее принятия
                                            ds.getRef().child("applicationStage").setValue(2);
                                            applicationDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            ValueEventListener valueEventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getUserId().equals(userId)) {
                                            //Добавление номера комнаты в профиль пользователя
                                            ds.getRef().child("room").setValue(numberRoom);
                                            userRefDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            userRefDB.addListenerForSingleValueEvent(valueEventListener1);
                            applicationDB.addListenerForSingleValueEvent(valueEventListener);
                            //Отображение информации куда заселен пользователь
                            Toast.makeText(getApplicationContext(),"Заселен(а) в " + numberRoom, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();

                        } else if (firstDocument.getString("2_1").equals("✖")) {
                            roomsCollectionRef.document(numberRoom).update("2_1", userId);
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Application application = ds.getValue(Application.class);
                                        if (application.getUserId().equals(userId)) {
                                            ds.getRef().child("applicationStage").setValue(2);
                                            applicationDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            ValueEventListener valueEventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getUserId().equals(userId)) {
                                            ds.getRef().child("room").setValue(numberRoom);
                                            userRefDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            userRefDB.addListenerForSingleValueEvent(valueEventListener1);
                            applicationDB.addListenerForSingleValueEvent(valueEventListener);
                            Toast.makeText(getApplicationContext(),"Заселен(а) в " + numberRoom, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();

                        } else  {
                            roomsCollectionRef.document(numberRoom).update("2_2", userId);
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Application application = ds.getValue(Application.class);
                                        if (application.getUserId().equals(userId)) {
                                            ds.getRef().child("applicationStage").setValue(2);
                                            applicationDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            ValueEventListener valueEventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getUserId().equals(userId)) {
                                            ds.getRef().child("room").setValue(numberRoom);
                                            userRefDB.removeEventListener(this);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };

                            userRefDB.addListenerForSingleValueEvent(valueEventListener1);
                            applicationDB.addListenerForSingleValueEvent(valueEventListener);
                            Toast.makeText(getApplicationContext(),"Заселен(а) в " + numberRoom, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(),"Невозможно заселить данного пользователя, возможно нет мест.", Toast.LENGTH_SHORT).show();
                });
    }

    /*
    onClickNo - функция для отклонения заявки на заселение.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    */
    public void onClickNo(View view) {

    }
}