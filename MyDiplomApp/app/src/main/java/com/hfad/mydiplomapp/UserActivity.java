/*
Класс: UserActivity
Язык: Java
Краткое описание:
Класс отображающий окно пользователя.

Глобальные переменные:
    mAuth - инициализация FirebaseAuth;
    cUser - получение текущего пользователя;
    applicationDB - инициализация базы данных приложения;
    userDB - инициализация базы данных пользователей;
    applicationRefDB - получение ссылки на узел "Application" в базе данных;
    userRefDB - получение ссылки на узел "User" в базе данных;
    fio - отображение ФИО пользователя;
    data - дата рождения пользователя;
    numberRoom - номер комнаты проживающего;
    posititon - очередь поданной заявки.

Функции, используемая в форме:
    onCreate – функция для вызова формы "activty_user";
    getFioDataUser - функция для получения ФИО пользвоателя;
    getNumberRoom - функция для получения номера комнаты пользователя;
    onClickOk - функция для подтверждения, что пользователя ознакомлся с информацией;
    onClickApplication - переход в окно подачи заявки;
    onClickContact - переход в окно информации "activity_info_contact";
    onClickRules - переход в окно информации "activity_info_rules";
    onClickBrunch - переход в окно информации "activity_info_branch";
    onClickBackEnter - переход в окно входа и закрытие текущего окна.
*/
package com.hfad.mydiplomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.mydiplomapp.callback.CallbackFioData;
import com.hfad.mydiplomapp.callback.CallbackRoom;
import com.hfad.mydiplomapp.classApp.Application;
import com.hfad.mydiplomapp.classApp.User;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private FirebaseDatabase userDB;
    private FirebaseDatabase applicationDB;
    private DatabaseReference applicationRefDB;
    private DatabaseReference userRefDB;
    private String fio;
    private String data;
    private String numberRoom;
    private int position;

    /*
    onCreate – функция для вызова формы "activty_user".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Глобальные переменные:
        applicationDB - инициализация базы данных приложения;
        userDB - инициализация базы данных пользователей;
        applicationRefDB - получение ссылки на узел "Application" в базе данных;
        userRefDB - получение ссылки на узел "User" в базе данных;
        mAuth - инициализация FirebaseAuth;
        cUser - получение текущего пользователя;
    Локальные переменные:
        itemHome - икнока главной вкладки;
        itemInfo - икнока информационной вкладки;
        itemPerson - икнока вкладки профиля;
        home - фрагмент для отображения главной вкалдки;
        info - фрагмент для отображения информационной вкалдки;
        person - фрагмент для отображения вкалдки профиля;
        person_submit - фрагмент отображающий этап профиля на стадии не поданной заявки;
        person_terminate - фрагмент отображающий этап профиля на стадии рассмотрения заявки;
        person_accpected - фрагмент отображающий этап профиля на стадии принятой заявки;
        home_submit - фрагмент отображающий этап главной вкладки на стадии не поданной заявки;
        home_terminate - фрагмент отображающий этап главной вкладки на стадии рассмотрения заявки;
        home_accept - фрагмент отображающий этап главной вкладки на стадии приянтия заявки;
        home_live - фрагмент отображающий этап главной вкладки на стадии проживания студента;
        home_kick - фрагмент отображающий этап главной вкладки на стадии выселении студента
        textFio - переменная для хранения ФИО пользователя;
        textData - переменная для хранения даты рождения пользователя;
        textEmailAccpected - переменная для хранения электроннйо почты пользователя на стадии принятия заявки;
        textFioAccpected - переменная для хранения ФИО пользователя на стадии принятия заявки;
        textDataAccpected - переменная для хранения даты рождения пользователя на стадии принятия заявки;
        intent - получение данных из окна после принятия заявки;
        stage - стадия поданной заявки;
        home_position - отображение очереди на вкладке главная;
        person_position - отображение очереди на вкладке профиля;
        positionString - текстовый формат очереди заявки;
        textView - номер комнаты пользователя во вкладке профиля;
        textView2 - номер комнаты пользователя в главной вкладке;
        EmailText - переменная с электронной почтой пользователя;
        email - поле для отображения электронной почты пользователя
        bottomNavigationView - нижнее навигационное меню.
    Функции, используемая в форме:
        getFioDataUser - функция для получения ФИО пользвоателя;
        getNumberRoom - функция для получения номера комнаты пользователя.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //Иницилизация переменных
        userDB  = FirebaseDatabase.getInstance();
        applicationDB = FirebaseDatabase.getInstance();
        applicationRefDB = applicationDB.getReference("Application");
        userRefDB = userDB.getReference("User");

        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();

        BottomNavigationItemView itemHome = (BottomNavigationItemView) findViewById(R.id.icon_home);
        BottomNavigationItemView itemInfo = (BottomNavigationItemView) findViewById(R.id.icon_info);
        BottomNavigationItemView itemPerson = (BottomNavigationItemView) findViewById(R.id.icon_person);

        itemHome.setActivated(true);

        FrameLayout home = (FrameLayout) findViewById(R.id.layout_home);
        FrameLayout info = (FrameLayout) findViewById(R.id.layout_info);
        FrameLayout person = (FrameLayout) findViewById(R.id.layout_person);

        FrameLayout person_submit = (FrameLayout) findViewById(R.id.applicationSubmit);
        FrameLayout person_terminate = (FrameLayout) findViewById(R.id.applicationTreatment);
        FrameLayout person_accpected = (FrameLayout) findViewById(R.id.applicationAccepted);

        FrameLayout home_submit = (FrameLayout) findViewById(R.id.home_applicationSubmit);
        FrameLayout home_terminate = (FrameLayout) findViewById(R.id.home_applicationTreatment);
        FrameLayout home_accept = (FrameLayout) findViewById(R.id.home_applicationAccept);
        FrameLayout home_live = (FrameLayout) findViewById(R.id.home_applicationLive);
        FrameLayout home_kick = (FrameLayout) findViewById(R.id.home_applicationKick);

        TextView textFio = (TextView) findViewById(R.id.fioUser);
        TextView textData = (TextView) findViewById(R.id.dataUser);

        TextView textEmailAccpected = (TextView) findViewById(R.id.emailUserAccpected);
        TextView textFioAccpected = (TextView) findViewById(R.id.fioUserAccpected);
        TextView textDataAccpected = (TextView) findViewById(R.id.dataUserAccpected);

        //Получение данных с окна подачи заявки
        Intent intent = getIntent();
        int stage = intent.getIntExtra("stage", 10);

        //Вывод профиля пользователя при не поданной заявки
        if (stage == 0) {
            person_submit.setVisibility(View.VISIBLE);
            person_terminate.setVisibility(View.GONE);

            home_submit.setVisibility(View.VISIBLE);
            //Вывод профиля пользователя при поданной заявки заявки
        } else if (stage == 1) {
            person_terminate.setVisibility(View.VISIBLE);
            person_submit.setVisibility(View.GONE);

            home_submit.setVisibility(View.GONE);
            home_terminate.setVisibility(View.VISIBLE);

            getFioDataUser(new CallbackFioData.Callback() {
                @Override
                public void onCallbackFioDataPosition(String fio, String data, int position) {
                    textFio.setText(fio);
                    textData.setText(data);

                    TextView home_position = (TextView) findViewById(R.id.home_textPosition);
                    TextView person_position = (TextView) findViewById(R.id.person_textPosition);

                    String positionString = String.valueOf(position + 1);

                    home_position.setText("Ваша позиция: " + positionString);
                    person_position.setText("Ваша позиция: " + positionString);
                }

            });
            //Вывод профиля пользователя при приянтой заявки
        } else if (stage == 2) {
            person_terminate.setVisibility(View.GONE);
            person_submit.setVisibility(View.GONE);
            person_accpected.setVisibility(View.VISIBLE);

            home_submit.setVisibility(View.GONE);
            home_terminate.setVisibility(View.GONE);
            home_accept.setVisibility(View.VISIBLE);

            getFioDataUser(new CallbackFioData.Callback() {
                @Override
                public void onCallbackFioDataPosition(String fio, String data, int position) {
                    textEmailAccpected.setText(cUser.getEmail());
                    textFioAccpected.setText(fio);
                    textDataAccpected.setText(data);
                }
            });

            getNumberRoom(new CallbackRoom.Callback() {

                @Override
                public void onCallbackRoom(String room) {
                    TextView textView = findViewById(R.id.roomUser);
                    textView.setText("Блок: " + numberRoom);
                }
            });
            //Вывод профиля пользователя при его выселении
        } else if (stage == 3) {
            home_submit.setVisibility(View.GONE);
            home_terminate.setVisibility(View.GONE);
            home_kick.setVisibility(View.VISIBLE);

            person_terminate.setVisibility(View.GONE);
            person_submit.setVisibility(View.GONE);
            person_accpected.setVisibility(View.VISIBLE);

            getFioDataUser(new CallbackFioData.Callback() {
                @Override
                public void onCallbackFioDataPosition(String fio, String data, int position) {
                    textEmailAccpected.setText(cUser.getEmail());
                    textFioAccpected.setText(fio);
                    textDataAccpected.setText(data);
                }
            });

            getNumberRoom(new CallbackRoom.Callback() {

                @Override
                public void onCallbackRoom(String room) {
                    TextView textView = findViewById(R.id.roomUser);

                    if (numberRoom != null) {
                        textView.setText("Блок: " + numberRoom);
                    } else {
                        textView.setText("Вы выселены \uD83D\uDE44");
                    }
                }
            });
            //Вывод профиля пользователя при проживании в общежитии
        } else if (stage == 4) {
            home_submit.setVisibility(View.GONE);
            home_terminate.setVisibility(View.GONE);
            home_kick.setVisibility(View.GONE);
            home_live.setVisibility(View.VISIBLE);

            person_terminate.setVisibility(View.GONE);
            person_submit.setVisibility(View.GONE);
            person_accpected.setVisibility(View.VISIBLE);

            getFioDataUser(new CallbackFioData.Callback() {
                @Override
                public void onCallbackFioDataPosition(String fio, String data, int position) {
                    textEmailAccpected.setText(cUser.getEmail());
                    textFioAccpected.setText(fio);
                    textDataAccpected.setText(data);
                }
            });

            getNumberRoom(new CallbackRoom.Callback() {

                @Override
                public void onCallbackRoom(String room) {
                    TextView textView = findViewById(R.id.home_textRoom_live);
                    TextView textView2 = findViewById(R.id.roomUser);
                    textView.setText("Ваш блок прживания: " + numberRoom);
                    textView2.setText("Блок: " + numberRoom);
                }
            });
        }

        TextView EmailText = (TextView) findViewById(R.id.emailUser);
        String email = cUser.getEmail().toString();
        EmailText.setText(email);

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
    getFioDataUser - функция для получения ФИО пользвоателя.
    Формальный параметр:
        callback - параметр для ожидания изменения переменных.
    Глобальные переменные:
        fio - отображение ФИО пользователя;
        data - дата рождения пользователя;
        applicationRefDB - получение ссылки на узел "Application" в базе данных;
        posititon - очередь поданной заявки.
    Локальные переменные:
        valueEventListener - слушатель для чтения данных из БД заявок;
        index - переменная для получения очереди заявки;
        application - переменная для работы с данными из БД;
    */
    private void getFioDataUser(CallbackFioData.Callback callback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = -1;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    if (application.getApplicationStage() == 1) {
                        index++;
                    }
                    if (application.getUserId().equals(cUser.getUid())) {
                        fio = application.getLastName() + " " + application.getName() + " " + application.getSureName();
                        data = application.getData();
                        position = index;
                        callback.onCallbackFioDataPosition(fio, data, position);
                        break;
                    }
                }

                // Удаление слушателя после обработки данных
                applicationRefDB.removeEventListener(this);

                if (callback != null) {
                    callback.onCallbackFioDataPosition(fio, data, position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибки
            }
        };

        applicationRefDB.addValueEventListener(valueEventListener);
    }

    /*
    getNumberRoom - функция для получения номера комнаты пользователя.
    Формальный параметр:
        callbackRoom - параметр для ожидания измения переменных.
    Глобальные переменные:
        userRefDB - получение ссылки на узел "User" в базе данных;
        numberRoom - номер комнаты проживающего.
    Локальные переменные:
        valueEventListener1 - слушатель для чтения данных из БД пользователей;
        user - переменная для работы с данными из БД.
    */
    private void getNumberRoom(CallbackRoom.Callback callbackRoom) {
        //Описание слушателя
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    //Внесение данных в переменные при верном условии
                    if (user.getUserId().equals(cUser.getUid())) {
                        numberRoom = user.getRoom();
                        callbackRoom.onCallbackRoom(numberRoom);
                    }
                }

                userRefDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        if (callbackRoom != null) {
            callbackRoom.onCallbackRoom(numberRoom);
        }

        //Запуск слушателя
        userRefDB.addValueEventListener(valueEventListener1);
    }

    /*
    onClickOk - функция для подтверждения, что пользователя ознакомлся с информацией.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        cUser - получение текущего пользователя;
        applicationRefDB - получение ссылки на узел "Application" в базе данных.
    Локальные переменные:
        valueEventListener - слушатель для чтения данных из БД заявок;
        application - переменная для работы с данными из БД;
        intent - переменная для перехода в окно "activity_user".
    */
    public void onClickOk (View view) {
        //Описание слушателя
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    if (application.getUserId().equals(cUser.getUid())) {
                        //Присвоение заявки новой стадии (проживание в общежитии)
                        ds.getRef().child("applicationStage").setValue(4);
                        break;
                    }
                }

                userRefDB.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //Запуск слушателя
        applicationRefDB.addValueEventListener(valueEventListener);

        Intent intent = new Intent(this, UserActivity.class);
        //Запуск окна пользователя
        startActivity(intent);
    }

    /*
    onClickApplication - переход в окно подачи заявки.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переменная для перехода в окно "activity_application".
    */
    public void onClickApplication(View view) {
        Intent intent = new Intent(this, ApplicationActivity.class);
        //Запуск окна для подачи заявки
        startActivity(intent);
        finish();
    }

    /*
    onClickContact - переход в окно информации "activity_info_contact".
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переменная для перехода в окно "activity_info_contact".
    */
    public void onClickContact(View view) {
        Intent intent = new Intent(this, InfoContactActivity.class);
        //Запуск информационного окна "activity_info_contact"
        startActivity(intent);
    }

    /*
    onClickRules - переход в окно информации "activity_info_rules".
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переменная для перехода в окно "activity_info_rules".
    */
    public void onClickRules(View view) {
        Intent intent = new Intent(this, InfoRulesActivity.class);
        //Запуск информационного окна "activity_info_rules"
        startActivity(intent);
    }

    /*
    onClickBrunch - переход в окно информации "activity_info_branch".
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переменная для перехода в окно "activity_info_branch".
    */
    public void onClickBrunch(View view) {
        Intent intent = new Intent(this, InfoBranchActivity.class);
        //Запуск информационного окна "activity_info_branch"
        startActivity(intent);
    }

    /*
    onClickBackEnter - переход в окно входа и закрытие текущего окна.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переменная для перехода в окно входа.
    */
    public void onClickBackEnter(View view) {
        Intent intent = new Intent(this, EnterActivity.class);
        //Запуска окна входа в аккаунт
        startActivity(intent);
        finish();
    }
}