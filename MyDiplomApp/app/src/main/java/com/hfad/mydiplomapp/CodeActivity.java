/*
Класс: CodeActivity
Язык: Java
Краткое описание:
Класс для отображения информации об отправленном письме на почту.

Функция, используемая в форме:
    onCreate – функция для вызова формы "activty_code";
    onClickSend - функция для перехода в окно "activity_enter".
*/
package com.hfad.mydiplomapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CodeActivity extends AppCompatActivity {

    /*
    onCreate - функция для запуска формы "activty_code".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
    }

    /*
    onClickSend - функция для перехода в окно "activity_enter"
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Локальные переменные:
        intent - переменная для переход на форму "activity_enter".
    */
    public void onClickSend(View view) {
        Intent intent = new Intent(this, EnterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}