/*
Класс: InfoBranchActivity
Язык: Java
Краткое описание:
Класс для отображения преимуществ данного общежития.

Функция, используемая в форме:
    onCreate – функция для вызова формы "activty_info_branch";
    onClickBack - функция для закрытия текущего окна.
*/
package com.hfad.mydiplomapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class InfoBranchActivity extends AppCompatActivity {

    /*
    onCreate - функция для запуска формы "activty_info_branch".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_branch);
    }

    /*
    onClickBack - функция для закрытия текущего окна.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    */
    public void onClickBack(View view) {
        finish();
    }
}