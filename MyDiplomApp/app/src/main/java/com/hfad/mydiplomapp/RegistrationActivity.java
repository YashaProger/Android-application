/*
Класс: RegistrationActivity
Язык: Java
Краткое описание:
Класс для регистрации пользователя в программе.

Глобальные переменные:
    mAuth – аунтефикация пользователей в приложении;
    regular – регулярные выражения для проверки полей.

Функция, используемая в форме:
    onCreate – функция для вызова формы "activty_registration";
    init – функция инициализации переменных;
    onClickRegistration - функция для регистрации пользователя.
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
import com.hfad.mydiplomapp.classApp.Regular;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Regular regular = new Regular();

    /*
    onCreate - функция для запуска формы "activty_registration".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Функция, используемая в форме:
        init – функция инициализации переменных.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
    }
    /*
    init - функция инициализации переменных.
    Глобальные переменные:
        mAuth - инициализация FirebaseAuth.
    */
    private void init() {
        mAuth = FirebaseAuth.getInstance();
    }

    /*
    onClickRegistration - функция для регистрации пользователя.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        mAuth – аунтефикация пользователей в приложении;
        regular – регулярные выражения для проверки полей.
    Локальные переменные:
        mailText - переменная для получения данных из поля "почта";
        passwordReg - переменная для получения данных из поля "пароль";
        passwordRepit - переменная для получения данных из поля "повторить пароль";
        mailReg - перменная для хранения данных из поля "почта";
        passwordReg - перемнная для хранения даннхы из поля "пароль";
        passwordRepit - перемнная для хранения даннхы из поля "повторить пароль";
        intent - переменная для переход на форму "activity_code".
    */
    public void onClickRegistration(View view) {
        //Получение значений с полей ввода
        EditText mailText = (EditText) findViewById(R.id.emailRegistration);
        EditText passwordText = (EditText) findViewById(R.id.passwordRegistration);
        EditText passwordTextRepit = (EditText) findViewById(R.id.passwordRegistrationRepit);

        String mailReg = mailText.getText().toString();
        String passwordReg = passwordText.getText().toString();
        String passwordRepit = passwordTextRepit.getText().toString();

        Intent intent = new Intent(this, CodeActivity.class);
        intent.putExtra("mail", mailReg);
        //Проверка значений взятых с полей ввода
       if (regular.isMail(mailReg)) {
            if (regular.isPassword(passwordReg)) {
                if (passwordReg.equals(passwordRepit)) {
                    mAuth.createUserWithEmailAndPassword(mailReg, passwordReg).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Отправка ссылки для прохождения верефикации
                                mAuth.getCurrentUser().sendEmailVerification();
                                //Переход на форму "activity_code"
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                           } else {
                                Toast.makeText(getApplicationContext(),"Ошибка, введены не корректные данные!", Toast.LENGTH_SHORT).show();
                            }

                        }});
                } else {
                    Toast.makeText(getApplicationContext(),"Введенные пароли не совпадают.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Пароль должен содержать минимум 8 символов.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Почта введена не корректно.", Toast.LENGTH_SHORT).show();
        }
    }
}