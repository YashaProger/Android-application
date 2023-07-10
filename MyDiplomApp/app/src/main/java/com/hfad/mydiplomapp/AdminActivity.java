/*
Класс: AdminActivity
Язык: Java
Краткое описание:
Класс отображающий окно администратора.

Глобальные переменные:
    floor - массив с этажми в общежитии;
    applicationDB - получение ссылки на узел "Application" в базе данных;
    userDB - инициализация базы данных пользователей;
    userRefDB - получение ссылки на узел "User" в базе данных;
    db - подключение к базе данных комнат;
    manApplication - поданные мужские заявки;
    womenApplication - поданные женские заявки;
    manFree - колличсетво мужских свободных мест;
    womenFree - колличсетво женских свободных мест;
    chetManApplication - переменная для подсчета поданных мужских заявок;
    chetWomenApplication - переменная для подсчета поданных женских заявок.

Функции, используемая в форме:
    onCreate – функция для вызова формы "activty_admin";
    onClickInfoRoom - функция для получение информации о комнате;
    onClickInfoRoomExit - функция для закрытия информации о комнате;
    getInfoUser - получение информации о пользователе, который живет в комнате;
    getChetApplication - получение информации о колличестве поданных заявок;
    getChetFree - получение информации о колличестве свободных мест в общежитии;
    onClickApplication - переход в окно поданных заявок;
    onClickListUser - переход в окно со списком проживающих;
    onClickBackEnter - переход в окно входа и закрытие текущего окна.
*/
package com.hfad.mydiplomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hfad.mydiplomapp.callback.CallbackChet;
import com.hfad.mydiplomapp.callback.CallbackInfoUser;
import com.hfad.mydiplomapp.classApp.Application;
import com.hfad.mydiplomapp.classApp.User;

import java.util.Arrays;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private String[] floor = {"Этаж 4", "Этаж 5", "Этаж 6", "Этаж 13"};
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabase userDB;
    private DatabaseReference userRefDB;
    private DatabaseReference applicationDB = FirebaseDatabase.getInstance().getReference("Application");

    private TextView manApplication;
    private TextView womenApplication;
    private TextView manFree;
    private TextView womenFree;

    private int chetManApplication;
    private int chetWomenApplication;

    /*
    onCreate – функция для вызова формы "activty_admin".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Глобальные переменные:
        userDB - инициализация базы данных пользователей;
        userRefDB - получение ссылки на узел "User" в базе данных;
        manApplication - поданные мужские заявки;
        womenApplication - поданные женские заявки;
        manFree - колличсетво мужских свободных мест;
        womenFree - колличсетво женских свободных мест;
    Локальные переменные:
        adFloor - массив с перечесленными этажами общежития;
        spiner - выпадающий список с этажами общежития;
        buttonRoom - список номерами комнат в общежитии на каждом этаже;
        floor - переменная для получения данных из выпадающего списка;
        number - выводящиеся номера комнат;
        home - фрагмент для отображения списка комнат;
        info - фрагмент для отображения поданных заявок;
        person - фрагмент для отображения профиля и списка проживающих;
        itemHome - икнока главной вкладки;
        itemInfo - икнока информационной вкладки;
        itemPerson - икнока вкладки профиля;
        bottomNavigationView - нижнее навигационное меню.
    Используемые функции:
        getChetApplication - получение информации о колличестве поданных заявок;
        getChetFree - получение информации о колличестве свободных мест в общежитии.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Иницилизация переменных
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userDB = FirebaseDatabase.getInstance();
        userRefDB = userDB.getReference("User");

        //Созлание выпадающего списка с этажами общежития
        ArrayAdapter<String> adFloor = new ArrayAdapter<String>(this, R.layout.spinner_selector_item, floor);
        adFloor.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spiner = (Spinner) findViewById(R.id.spFloor);
        spiner.setAdapter(adFloor);

        //Создание кнопок для отображения информации в комнате
        Button[] buttonRoom = {(Button) findViewById(R.id.button_room_1), (Button) findViewById(R.id.button_room_2), (Button) findViewById(R.id.button_room_3),
                (Button) findViewById(R.id.button_room_4), (Button) findViewById(R.id.button_room_5), (Button) findViewById(R.id.button_room_6), (Button) findViewById(R.id.button_room_7),
                (Button) findViewById(R.id.button_room_8), (Button) findViewById(R.id.button_room_9),  (Button) findViewById(R.id.button_room_10), (Button) findViewById(R.id.button_room_11),
                (Button) findViewById(R.id.button_room_12),  (Button) findViewById(R.id.button_room_13), (Button) findViewById(R.id.button_room_14)};

        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String floor = parent.getSelectedItem().toString();
                if (floor.equals("Этаж 4")) {
                    for (int i = 0; i <= buttonRoom.length-1; i++) {
                        String number = String.valueOf(i+1);
                        if (i <= 8) {
                            buttonRoom[i].setText("40" + number);
                        } else {
                            buttonRoom[i].setText("4" + number);
                        }
                    }
                } else if (floor.equals("Этаж 5")) {
                    for (int i = 0; i <= buttonRoom.length-1; i++) {
                        String number = String.valueOf(i+1);
                        if (i <= 8) {
                            buttonRoom[i].setText("50" + number);
                        } else {
                            buttonRoom[i].setText("5" + number);
                        }
                    }
                } else if (floor.equals("Этаж 6")) {
                    for (int i = 0; i <= buttonRoom.length-1; i++) {
                        String number = String.valueOf(i+1);
                        if (i <= 8) {
                            buttonRoom[i].setText("60" + number);
                        } else {
                            buttonRoom[i].setText("6" + number);
                        }
                    }
                } else {
                    for (int i = 0; i <= buttonRoom.length-1; i++) {
                        String number = String.valueOf(i+1);
                        if (i <= 8) {
                            buttonRoom[i].setText("130" + number);
                        } else {
                            buttonRoom[i].setText("13" + number);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        manApplication = (TextView) findViewById(R.id.application_man);
        womenApplication = (TextView) findViewById(R.id.application_women);

        //Подсчет поданных заявок
        getChetApplication(new CallbackChet.Callback() {
            @Override
            public void onCallbackChet(int chetMen, int chetWomen) {
                manApplication.setText("Мужской блок: " + chetManApplication);
                womenApplication.setText("Женский блок: " + chetWomenApplication);
            }
        });

        manFree = (TextView) findViewById(R.id.free_place_man);
        womenFree = (TextView) findViewById(R.id.free_place_women);

        //Подсчет свободных метс
        getChetFree(new CallbackChet.Callback() {
            @Override
            public void onCallbackChet(int chetMen, int chetWomen) {
                manFree.setText("Мужской блок: " + chetMen);
                womenFree.setText("Женский блок: " + chetWomen);
            }
        });

        FrameLayout home = (FrameLayout) findViewById(R.id.layout_home);
        FrameLayout info = (FrameLayout) findViewById(R.id.layout_info);
        FrameLayout person = (FrameLayout) findViewById(R.id.layout_person);

        BottomNavigationItemView itemHome = (BottomNavigationItemView) findViewById(R.id.icon_home);
        BottomNavigationItemView itemInfo = (BottomNavigationItemView) findViewById(R.id.icon_info);
        BottomNavigationItemView itemPerson = (BottomNavigationItemView) findViewById(R.id.icon_person);

        itemHome.setActivated(true);

        //Создание нижней панели навигации
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.button_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.icon_home:
                    home.setVisibility(View.VISIBLE);
                    info.setVisibility(View.GONE);
                    person.setVisibility(View.GONE);
                    itemHome.setActivated(true);
                    itemInfo.setActivated(false);
                    itemPerson.setActivated(false);
                    break;
                case R.id.icon_info:
                    home.setVisibility(View.GONE);
                    info.setVisibility(View.VISIBLE);
                    person.setVisibility(View.GONE);
                    itemHome.setActivated(false);
                    itemInfo.setActivated(true);
                    itemPerson.setActivated(false);
                    break;
                case R.id.icon_person:
                    home.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    person.setVisibility(View.VISIBLE);
                    itemHome.setActivated(false);
                    itemInfo.setActivated(false);
                    itemPerson.setActivated(true);
                    break;
            }
            return false;
        });
    }

    /*
    onClickInfoRoom - функция для получение информации о комнате.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальная переменная:
        db - подключение к базе данных комнат.
    Локальные переменные:
        numberRoom - получение номера комнаты при нажатии;
        roomsCollectionRef - сыылка для подключения к базе данных комнат;
        documentID - принмает переменную "numberRoom";
        documentReference - получение конкретной комнаты из БД;
        block - отображает номер комнаты;
        room_3_1 - переменная для отображения пользователя проживающего в комнате;
        room_3_2 - переменная для отображения пользователя проживающего в комнате;
        room_3_3 - переменная для отображения пользователя проживающего в комнате;
        room_2_1 - переменная для отображения пользователя проживающего в комнате;
        room_2_2 - переменная для отображения пользователя проживающего в комнате;
        infoRoom - отображает фрагмент информацию о комнате.
    Используемая функция:
        getInfoUser - получение информации о пользователе, который живет в комнате.
    */
    public void onClickInfoRoom(View view) {
        //Иницилизация переменных
        String numberRoom = ((Button) view).getText().toString();

        CollectionReference roomsCollectionRef = db.collection("rooms");

        String documentID = numberRoom;
        DocumentReference documentReference = roomsCollectionRef.document(documentID);

        TextView block = findViewById(R.id.block);

        TextView room_3_1 = findViewById(R.id.room_3_1);
        TextView room_3_2 = findViewById(R.id.room_3_2);
        TextView room_3_3 = findViewById(R.id.room_3_3);

        TextView room_2_1 = findViewById(R.id.room_2_1);
        TextView room_2_2 = findViewById(R.id.room_2_2);

        //Получение информации о проживающих в комнате
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                getInfoUser(documentSnapshot.getString("2_1"), new CallbackInfoUser.Callback() {
                    @Override
                    public void onCallbackInfoUser(String info) {
                        room_2_1.setText(info);
                    }
                });
                getInfoUser(documentSnapshot.getString("2_2"), new CallbackInfoUser.Callback() {
                    @Override
                    public void onCallbackInfoUser(String info) {
                        room_2_2.setText(info);
                    }
                });
                getInfoUser(documentSnapshot.getString("3_1"), new CallbackInfoUser.Callback() {
                    @Override
                    public void onCallbackInfoUser(String info) {
                        room_3_1.setText(info);
                    }
                });
                getInfoUser(documentSnapshot.getString("3_2"), new CallbackInfoUser.Callback() {
                    @Override
                    public void onCallbackInfoUser(String info) {
                        room_3_2.setText(info);
                    }
                });
                getInfoUser(documentSnapshot.getString("3_3"), new CallbackInfoUser.Callback() {
                    @Override
                    public void onCallbackInfoUser(String info) {
                        room_3_3.setText(info);
                    }
                });

                block.setText(numberRoom + " блок");
                FrameLayout infoRoom = (FrameLayout) findViewById(R.id.info_room);
                infoRoom.setVisibility(View.VISIBLE);
            } else {
                block.setText(numberRoom + " блок");
                room_2_1.setText(documentSnapshot.getString("✖"));
                room_2_2.setText(documentSnapshot.getString("✖"));
                room_3_1.setText(documentSnapshot.getString("✖"));
                room_3_2.setText(documentSnapshot.getString("✖"));
                room_3_3.setText(documentSnapshot.getString("✖"));
                //Отображение фрагмента информации о комнате
                FrameLayout infoRoom = (FrameLayout) findViewById(R.id.info_room);
                infoRoom.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(e -> {

        });
    }

    /*
    onClickInfoRoom - функция для получение информации о комнате.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        infoRoom - отображает фрагмент информацию о комнате.
    */
    public void onClickInfoRoomExit(View view) {
        FrameLayout infoRoom = (FrameLayout) findViewById(R.id.info_room);
        //Закртие окна информации о комнате
        infoRoom.setVisibility(View.GONE);
    }

    /*
    getInfoUser - получение информации о пользователе, который живет в комнате.
    Формальные параметры:
        idUser - уникальный ключ пользователя для нахождения его в базе данных;
        callback - параметр для ожидания изменения переменных.
    Глобальная переменная:
        userRefDB - получение ссылки на узел "User" в базе данных.
    Локальные переменные:
        valueEventListener - слушатель для базы данных пользователей;
        userFound - переменная для подтверждения нахождения пользователя;
        info - отображение данных пользователя.
    */
    public void getInfoUser(String idUser, CallbackInfoUser.Callback callback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getUserId().equals(idUser)) {
                        String info = user.getLastName() + " " + user.getName().charAt(0) + ". " + user.getSureName().charAt(0) + ".";
                        callback.onCallbackInfoUser(info);
                        userFound = true;
                        break;
                    }
                }
                if (!userFound) {
                    String info = "✖";
                    callback.onCallbackInfoUser(info);
                }

                // Удаление слушателя после получения данных
                userRefDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallbackInfoUser("");
            }
        };

        userRefDB.addValueEventListener(valueEventListener);
    }

    /*
    getChetApplication - получение информации о колличестве поданных заявок.
    Формальный параметр:
        callback - параметр для ожидания изменения переменных.
    Глобальные переменные:
        applicationDB - получение ссылки на узел "Application" в базе данных;
        chetManApplication - переменная для подсчета поданных мужских заявок;
        chetWomenApplication - переменная для подсчета поданных женских заявок.
    Локальные переменные:
        valueEventListener - слушатель для базы данных поданных заявок;
        chetMan - счетчик поданных мужских заявок;
        chetWomen - счетчик поданных женских заявок;
        application - переменная для работы с данными из БД.
    */
    public void getChetApplication(CallbackChet.Callback callback) {
        //Создание слушателя
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int chetMan = 0;
                int chetWomen = 0;
                //Подсчет поданных заявок
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    if ((application.getGender().equals("Мужской")) && (application.getApplicationStage() == 1)) {
                        chetMan += 1;
                    } else if (application.getApplicationStage() == 1) {
                        chetWomen += 1;
                    }
                }
                //Запись итогов подсчета в глобальные переменные
                chetManApplication = chetMan;
                chetWomenApplication = chetWomen;
                if (callback != null) {
                    callback.onCallbackChet(chetManApplication, chetWomenApplication);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //Запуск слушателя
        applicationDB.addValueEventListener(valueEventListener);
        callback.onCallbackChet(chetManApplication, chetWomenApplication);
    }

    /*
    getChetFree - получение информации о колличестве свободных мест в общежитии.
    Формальный параметр:
        callback - параметр для ожидания изменения переменных.
    Локальные переменные:
        roomsCollectionRef - ссылка для подключения к базе данных комнат;
        chetMen - счетчик мужских свободных мест;
        chetWomen - счетчик женских свободных метс;
        fields - список коек в комнате.
    */
    public void getChetFree(CallbackChet.Callback callback) {
        //Иницилизация переменных
        CollectionReference roomsCollectionRef = db.collection("rooms");

        final int[] chetMen = {0};
        final int[] chetWomen = {0};

        List<String> fields = Arrays.asList("3_1", "3_2", "3_3", "2_1", "2_2");

        //Подсчет свободных мест в общежитии
        for (String field: fields) {
            roomsCollectionRef.whereEqualTo("gender", "Мужской")
                    .whereEqualTo(field, "✖")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        chetMen[0] += queryDocumentSnapshots.size();
                        callback.onCallbackChet(chetMen[0], chetWomen[0]);
                    })
                    .addOnFailureListener(e -> {

                    });

            roomsCollectionRef.whereEqualTo("gender", "Женский")
                    .whereEqualTo(field, "✖")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        chetWomen[0] += queryDocumentSnapshots.size();
                        callback.onCallbackChet(chetMen[0], chetWomen[0]);
                    })
                    .addOnFailureListener(e -> {

                    });
        }

        if (callback != null) {
            callback.onCallbackChet(chetMen[0], chetWomen[0]);
        }
    }

    /*
    onClickApplication - переход в окно поданных заявок.
    Формальный параметр:
         view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальная переменная:
        intent - переменная для запуска окна "activity_application_consider".
    */
    public void onClickApplication(View view) {
        Intent intent = new Intent(this, ApplicationConsiderActivity.class);
        //Запуск окна "activity_application_consider"
        startActivity(intent);
    }

    /*
    onClickListUser - переход в окно со списком проживающих.
    Формальный параметр:
         view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальная переменная:
        intent - переменная для запуска окна "activity_list_users".
    */
    public void onClickListUser(View view) {
        Intent intent = new Intent(this, ListUsers.class);
        //Запуск окна "activity_list_users"
        startActivity(intent);
    }

    /*
    onClickBackEnter - переход в окно входа и закрытие текущего окна.
    Формальный параметр:
         view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальная переменная:
        intent - переменная для запуска окна входа в аккаунт.
    */
    public void onClickBackEnter(View view) {
        Intent intent = new Intent(this, EnterActivity.class);
        //Запуск окна входа в аккаунт
        startActivity(intent);
        finish();
    }
}