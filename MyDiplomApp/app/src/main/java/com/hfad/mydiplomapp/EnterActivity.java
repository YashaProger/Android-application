/*
Дипломный проект по теме
"Разработка мобильного приложения для учета проживающих в общежитии"
Название приложения: MyDiplomApp

Разработал: Рубан Яков Андреевич, группа ТМП-81
Дата: 20.05.2023
Версия: 1.0
Язык: Java
Среда разработки: Android Studio.

********************************************************************************
Краткое описание:
Мобильное приложение для учета апроживающих в общежитии.
Задание:
Создать мобильное приложение для учета проживающих в общежитии. В приложении должны присутствовать формы авторизации и регистрации, формы для отображения проживающих в общежитии. Интерфейс должен быть интуитивно понятен.

********************************************************************************
Классы используемые в программе:
    EnterActivity – Класс для обрабатываемых событий в окне activity_enter;
    AdminActivity -	Класс для обрабатываемых событий в окне activity_admin
    ApplicationActivity - Класс для обрабатываемых событий в окне activity_application
    ApplicationConsiderActivity - Класс для обрабатываемых событий в окне activity_application_consider
    ApplicationUserActivity - Класс для обрабатываемых событий в окне activity_application_user
    CodeActivity - Класс для обрабатываемых событий в окне activity_code
    InfoBranchActivity - Класс для обрабатываемых событий в окне activity_info_branch
    InfoContactActivity - Класс для обрабатываемых событий в окне activity_info_contatct
    InfoRulesActivity - Класс для обрабатываемых событий в окне activity_info_rules
    ListUsers - Класс для отображения всех проживающих с возможностью их выселения
    RegistartionActivity - Класс для обрабатываемых событий в окне activity_registartion
    UserActivity - Класс для обрабатываемых событий в окне activity_user

********************************************************************************
Глобальные переменные используемые в классе EnterActivity:
    mAuth - инициализация FirebaseAuth;
    cUser - получение текущего пользователя;
    applicationDB - инициализация базы данных приложения;
    userDB - инициализация базы данных пользователей;
    applicationRefDB - получение ссылки на узел "Application" в базе данных;
    userRefDB - получение ссылки на узел "User" в базе данных.

********************************************************************************
Функции, используемые в классе EneterActivity:
    onCreate – функция для вызова формы "activty_enter";
    init – функция инициализации переменных;
    getApplicationStage – функция для получения стадии заявки;
    onClickEnter - функция дле перехода в форму "activity_enter";
    onClickRegistration - функция для перехода в форму "activity_registration".
*/

package com.hfad.mydiplomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.mydiplomapp.classApp.Application;
import com.hfad.mydiplomapp.callback.CallbackStage;
public class EnterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private FirebaseDatabase applicationDB;
    private FirebaseDatabase userDB;
    private DatabaseReference applicationRefDB;
    private DatabaseReference userRefDB;
    private int stage;

    /*
    onCreate - функция для запуска формы "activity_enter".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Функция, используемая в форме:
        init – функция инициализации переменных.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        init();
    }

    /*
    init - функция инициализации переменных.
    Глобальные переменные:
        mAuth - инициализация FirebaseAuth;
        cUser - получение текущего пользователя;
        applicationDB - инициализация базы данных приложения;
        userDB - инициализация базы данных пользователей;
        applicationRefDB - получение ссылки на узел "Application" в базе данных;
        userRefDB - получение ссылки на узел "User" в базе данных.
    */
    private void init() {
        //Иницилизация переменных
        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();
        applicationDB = FirebaseDatabase.getInstance();
        userDB = FirebaseDatabase.getInstance();
        applicationRefDB = applicationDB.getReference("Application");
        userRefDB = userDB.getReference("User");
    }

    /*
    getApplicationStage - функция для получения стадии заявки.
    Формальный параметр:
        callback - параметр для ожидания изменения переменных.
    Глобальные переменные:
        cUser - получение текущего пользователя;
        stage - этап заявки;
        applicationRef - получение ссылки на узел "Application" в базе данных.
    Локальные перемнные:
        valueEventListener - слушатель для чтения базы данных (БД);
        application - класс для получения данных из БД.
    */
    private void getApplicationStage(CallbackStage.Callback callback) {
        //Создание слушателя для базы данных
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    if (cUser.getUid().equals(application.getUserId())) {
                        //Получение стадии заявки
                        stage = application.getApplicationStage();
                        break;
                    } else {
                        stage = 0;
                    }
                }
                if (callback != null) {
                    callback.onCallbackStage(stage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //Запуск слушателя
        applicationRefDB.addValueEventListener(valueEventListener);
        callback.onCallbackStage(stage);
    }

    /*
    onClickEnter - функция дле перехода в форму "activity_enter".
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        cUser - получение текущего пользователя;
        mAuth - инициализация FirebaseAuth.
    Локальные перемнные:
        emailText - переменная для получения данных из поля "почта";
        passwordText - переменная для получения данных из поля "пароль";
        email - перменная для хранения данных из поля "почта";
        password - перемнная для хранения даннхы из поля "пароль";
        intentAdmin - переменная для переход на форму "activity_admin";
        intentUser - переменная для переход на форму "activity_user";
        intentCode - переменная для переход на форму "activity_code".
    */
    public void onClickEnter(View view) {
        //Получение данных из полей ввода "почта" и "пароль"
        EditText emailText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.password);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Intent intentAdmin = new Intent(this, AdminActivity.class);

        //Переход в аккаунт администратора по условию
        if (email.equals("admin") && (password.equals("admin"))) {
            startActivity(intentAdmin);
        }

        Intent intentUser = new Intent(this, UserActivity.class);
        Intent intentCode = new Intent(this, CodeActivity.class);

        //Переход пользователя в аккаунт
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (cUser.isEmailVerified()) {
                        getApplicationStage(new CallbackStage.Callback() {
                            @Override
                            public void onCallbackStage(int stage) {
                                intentUser.putExtra("stage", stage);
                                //Переход в окно "activity_user"
                                startActivity(intentUser);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        });
                    } else {
                        //Отправка ссылки на почту и переход в окно "activity_code"
                        mAuth.getCurrentUser().sendEmailVerification();
                        startActivity(intentCode);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                } else {
                    //Сообщение об ошибке
                    Toast.makeText(getApplicationContext(),"Такого пользователя не существует!", Toast.LENGTH_SHORT).show();
                }
            }});
    }

    /*
    onClickRegistration - функция дле перехода в форму "activity_registration".
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные перемнные:
        intent - переменная для переход на форму "activity_registration".
    */
    public void onClickRegistration(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        //Запуск окна "activity_registration"
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
