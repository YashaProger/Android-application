/*
Класс: ApplicationConsiderActivity
Язык: Java
Краткое описание:
Класс отображаюзий список всех поданных заявок.

Глобальные переменные:
    sort - массив с вариантами сортировки;
    applicationDB - инициализация базы данных приложения;
    applicationRefDB - получение ссылки на узел "Application" в базе данных;
    listView - список заявок;
    arrayAdapter - массив который адаптируется под БД;
    listData - список для правила отображения заявок заявок;
    listApplication - список с отображающимися заявками.

Функция, используемая в форме:
    onCreate – функция для вызова формы "activty_application_consider";
    getDataFromDBAll – функция для отображения всех заявок;
    getDataFromDBMen - функция для отображения мужских заявок;
    getDataFromDBWomen - функция для отображения женских заявок;
    setOnClickListItem - функция для рассмотрения конкретной заявки;
    onClickBackAdmin - функция для закрытия текущего окна.
*/
package com.hfad.mydiplomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.mydiplomapp.classApp.Application;

import java.util.ArrayList;
import java.util.List;

public class ApplicationConsiderActivity extends AppCompatActivity {
    private String[] sort = {"Все", "Муж", "Жен"};
    private FirebaseDatabase applicationDB;
    private DatabaseReference applicationRefDB;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> listData;
    private List<Application> listApplication;

    /*
    onCreate - функция для запуска формы "activty_application_consider".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Глобальные переменные:
        sort - массив с вариантами сортировки;
        applicationDB - инициализация базы данных приложения;
        applicationRefDB - получение ссылки на узел "Application" в базе данных;
        listView - список заявок;
        arrayAdapter - массив который адаптируется под БД;
        listData - список для правила отображения заявок заявок;
        listApplication - список с отображающимися заявками.
    Локальные переменные:
        adSort - стиль элеменов списка;
        spiner - список элементов;
        floor - вариант сортировки.
    Функции, используемая в форме:
        getDataFromDBAll – функция для отображения всех заявок;
        getDataFromDBMen - функция для отображения мужских заявок;
        getDataFromDBWomen - функция для отображения женских заявок.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Иницилизация переменных
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_consider);

        applicationDB = FirebaseDatabase.getInstance();
        applicationRefDB = applicationDB.getReference("Application");

        listView = findViewById(R.id.listView);
        listData = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(arrayAdapter);
        listApplication = new ArrayList<Application>();
        //Создание списка вариантов сортировки
        ArrayAdapter<String> adSort = new ArrayAdapter<String>(this, R.layout.spinner_selector_item, sort);
        adSort.setDropDownViewResource(R.layout.spinner_dropdown_item);

        Spinner spiner = (Spinner) findViewById(R.id.spFloor);
        spiner.setAdapter(adSort);
        //Условия сортировки
        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String floor = parent.getSelectedItem().toString();
                if (floor.equals("Все")) {
                    getDataFromDBAll();
                } else if (floor.equals("Муж")) {
                    getDataFromDBMen();
                } else {
                    getDataFromDBWomen();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setOnClickListItem();
    }

    /*
    getDataFromDBAll – функция для отображения всех заявок.
    Глобальные переменные:
        listData - список для правила отображения заявок заявок;
        listApplication - список с отображающимися заявками;
        arrayAdapter - массив который адаптируется под БД;
        applicationRefDB - получение ссылки на узел "Application" в базе данных.
    Локальные переменные:
        valueEventListener - прослушиватель базы данных;
        application - переменная для получения данных из БД.
    */
    private void getDataFromDBAll() {
        //Создание слушателя базы данных
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listData.size() > 0) {
                    listData.clear();
                }
                if (listApplication.size() > 0) {
                    listApplication.clear();
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    assert application != null;
                    if (application.getApplicationStage() == 1) {
                        //Добавление заявки в список
                        listData.add(application.getLastName() + " " + application.getName() + " " + application.getSureName());
                        listApplication.add(application);
                    }
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //Запуск слушателя
        applicationRefDB.addValueEventListener(valueEventListener);
    }

    /*
    getDataFromDBMen – функция для отображения всех заявок.
    Глобальные переменные:
        listData - список для правила отображения заявок заявок;
        listApplication - список с отображающимися заявками;
        arrayAdapter - массив который адаптируется под БД;
        applicationRefDB - получение ссылки на узел "Application" в базе данных.
    Локальные переменные:
        valueEventListener - прослушиватель базы данных;
        application - переменная для получения данных из БД.
    */
    private void getDataFromDBMen() {
        //Создание слушателя для базы данных
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listData.size() > 0) {
                    listData.clear();
                }
                if (listApplication.size() > 0) {
                    listApplication.clear();
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    assert application != null;
                    System.out.println("Stage men: " + application.getApplicationStage());
                    if ((application.getGender().equals("Мужской")) && (application.getApplicationStage() == 1)) {
                        //Добавление заявки в список
                        listData.add(application.getLastName() + " " + application.getName() + " " + application.getSureName());
                        listApplication.add(application);
                    }
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //Запуск слушателя
        applicationRefDB.addValueEventListener(valueEventListener);
    }

    /*
    getDataFromDBWomen – функция для отображения всех заявок.
    Глобальные переменные:
        listData - список для правила отображения заявок заявок;
        listApplication - список с отображающимися заявками;
        arrayAdapter - массив который адаптируется под БД;
        applicationRefDB - получение ссылки на узел "Application" в базе данных.
    Локальные переменные:
        valueEventListener - прослушиватель базы данных;
        application - переменная для получения данных из БД.
    */
    private void getDataFromDBWomen() {
        //Создание слушателя для базы данных
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (listData.size() > 0) {
                    listData.clear();
                }
                if (listApplication.size() > 0) {
                    listApplication.clear();
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    assert application != null;
                    System.out.println("Stage women: " + application.getApplicationStage());
                    if ((application.getGender().equals("Женский")) && (application.getApplicationStage() == 1)) {
                        //Добавление заявки в список
                        listData.add(application.getLastName() + " " + application.getName() + " " + application.getSureName());
                        listApplication.add(application);
                    }
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //Запуск слушателя
        applicationRefDB.addValueEventListener(valueEventListener);
    }

    /*
    setOnClickListItem - функция для рассмотрения конкретной заявки.
    Глобальные переменные:
        listView - список заявок;
        listApplication - список с отображающимися заявками.
    Локальные переменные:
        application - переменная для получения данных из БД;
        intentApplicationUser - переход в окно "activity_application_user".
    */
    private void setOnClickListItem() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Получение позиции нажатия на список
                Application application = listApplication.get(position);
                Intent intentApplicationUser = new Intent(ApplicationConsiderActivity.this, ApplicationUserActivity.class);
                //Перенос данных в окно "activity_application_user"
                intentApplicationUser.putExtra("applicationUserUID", application.getUserId());
                intentApplicationUser.putExtra("lastName", application.getLastName());
                intentApplicationUser.putExtra("name", application.getName());
                intentApplicationUser.putExtra("sureName", application.getSureName());
                intentApplicationUser.putExtra("bithday", application.getData());
                intentApplicationUser.putExtra("gender", application.getGender());
                intentApplicationUser.putExtra("imageId", application.getImageId());
                startActivity(intentApplicationUser);
            }
        });
    }
    /*
    onClickBackAdmin - функция для закрытия текущего окна.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переход в окно "activity_admin"
    */
    public void onClickBackAdmin(View view) {
        Intent intent = new Intent(this, AdminActivity.class);
        //Запуска окна "activity_admin"
        startActivity(intent);
        finish();
    }
}