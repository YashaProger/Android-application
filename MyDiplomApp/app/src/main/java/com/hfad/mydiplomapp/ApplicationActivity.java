/*
Класс: ApplicationActivity
Язык: Java
Краткое описание:
Класс для отправки заявки на заселение в общежитие.

Глобальные переменные:
    mAuth - инициализация FirebaseAuth;
    cUser - получение текущего пользователя;
    applicationDB - инициализация базы данных приложения;
    userDB - инициализация базы данных пользователей;
    applicationRefDB - получение ссылки на узел "Application" в базе данных;
    userRefDB - получение ссылки на узел "User" в базе данных;
    storageReference - получение ссылки в хранилище картинок;
    imageView - переменная для отображения фотографий пользователя;
    binding - пременная для работы с объектами в данном окне;
    genderText - переменная для записи половго признака;
    uploadUri - переменная для записи ссылки фотографии;
    applicationUser - переменная для записи данных введеных с заявки;
    newUser - переменная для создания профиля;
    regular – регулярные выражения для проверки полей;
    flag - переменная для проверки условия.

Функции, используемая в форме:
    onCreate – функция для вызова формы "activty_application";
    init – функция инициализации переменных;
    onClickSendApplication - функция для отправки заявки на заселение;
    onClickMen - функция для выбора мужского пола;
    onClickWomen - функция для выбора женского пола;
    onClickPhoto - функция для запуска получения фотографий с телефона;
    onClickUser - функция для закрытия текущего окна;
    onActivityResult - функция при нажатии на imageView;
    getImage - хранилище телефона;
    uploadImage - отправка фоторгафии в хранилище.
*/
package com.hfad.mydiplomapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hfad.mydiplomapp.callback.CallbackImage;
import com.hfad.mydiplomapp.classApp.Application;
import com.hfad.mydiplomapp.classApp.Regular;
import com.hfad.mydiplomapp.classApp.User;
import com.hfad.mydiplomapp.databinding.ActivityApplicationBinding;

import java.io.ByteArrayOutputStream;

public class ApplicationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private FirebaseDatabase userDB;
    private FirebaseDatabase applicationDB;
    private DatabaseReference applicationRefDB;
    private DatabaseReference userRefDB;
    private StorageReference storageReference;
    private ImageView imageView;
    private ActivityApplicationBinding binding;
    private String genderText;
    private String uploadUri;
    private Application applicationUser = new Application();
    private User newUser = new User();
    private Regular regular = new Regular();
    private boolean flag;

    /*
    onCreate - функция для запуска формы "activty_application".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Функция, используемая в форме:
        init – функция инициализации переменных.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        binding = ActivityApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle("   ");
    }

    /*
    init - функция инициализации переменных.
    Глобальные переменные:
        mAuth - инициализация FirebaseAuth;
        cUser - получение текущего пользователя;
        applicationDB - инициализация базы данных приложения;
        userDB - инициализация базы данных пользователей;
        applicationRefDB - получение ссылки на узел "Application" в базе данных;
        userRefDB - получение ссылки на узел "User" в базе данных;
        storageReference - получение ссылки в хранилище картинок;
        imageView - переменная для отображения фотографий пользователя;
        flag - переменная для проверки условия.
    */
    public void init() {
        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();
        userDB  = FirebaseDatabase.getInstance();
        applicationDB = FirebaseDatabase.getInstance();
        applicationRefDB = applicationDB.getReference("Application");
        userRefDB = userDB.getReference("User");
        storageReference = FirebaseStorage.getInstance().getReference("ImageApplication");
        imageView = findViewById(R.id.imageViewDocument);
        flag = false;
    }

    /*
    onClickSendApplication - функция для отправки заявки на заселение.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        imageView - переменная для отображения фотографий пользователя;
        applicationUser - переменная для записи данных введеных с заявки;
        applicationRefDB - получение ссылки на узел "Application" в базе данных;
        userRefDB - получение ссылки на узел "User" в базе данных;
        cUser - получение текущего пользователя;
        newUser - переменная для создания профиля;
        uploadUri - переменная для записи ссылки фотографии;
        flag - переменная для проверки условия;
        genderText - переменная для записи половго признака;
        regular – регулярные выражения для проверки полей.
    Локальные переменные:
        editTextName - получение данных с поля ввода;
        editTextLastName - получение данных с поля ввода;
        editTextSureName - получение данных с поля ввода;
        editTextData - получение данных с поля ввода;
        name - запись данных с полей ввода;
        lastName - запись данных с полей ввода;
        sureName - запись данных с полей ввода;
        data - запись данных с полей ввода;
        gender - запись данных выбранного полового признака.
    Функция, используемая в форме:
        uploadImage - отправка фоторгафии в хранилище.
    */
    public void onClickSendApplication(View view) {
        Toast.makeText(getApplicationContext(),"Подождите...", Toast.LENGTH_SHORT).show();
        imageView = findViewById(R.id.imageViewDocument);
        if (imageView.getDrawable() == null) {
            Toast.makeText(this, "Пожалуйста, выберите фото подтверждающее, что вы студент", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage(new CallbackImage.Callback() {
            @Override
            public void onCallbackImage(String uri) {
                //Получение и запись данных из полей ввода
                EditText editTextName = (EditText) findViewById(R.id.application_name);
                EditText editTextLastName = (EditText) findViewById(R.id.application_lastName);
                EditText editTextSureName = (EditText) findViewById(R.id.application_surname);
                EditText editTextData = (EditText) findViewById(R.id.application_date);

                String name = (String) editTextName.getText().toString();
                String lastName = (String) editTextLastName.getText().toString();
                String sureName = (String) editTextSureName.getText().toString();
                String data = (String) editTextData.getText().toString();
                String gender = genderText;

                //Проверка полученных данных
                if (regular.isFio(name)) {
                    if (regular.isFio(lastName)) {
                        if (regular.isFio(sureName)) {
                            if (regular.isBirthday(data)) {
                                if (gender != null) {
                                        //Запись данных в БД
                                        applicationUser.setId(applicationRefDB.push().getKey());
                                        applicationUser.setUserId(cUser.getUid().toString());
                                        applicationUser.setName(name);
                                        applicationUser.setLastName(lastName);
                                        applicationUser.setSureName(sureName);
                                        applicationUser.setData(data);
                                        applicationUser.setGender(gender);
                                        applicationUser.setImageId(uploadUri);
                                        applicationUser.setApplicationStage(1);
                                        newUser = new User(userRefDB.push().getKey(), cUser.getUid().toString(), lastName, name, sureName, data, gender, null);

                                        //Создание новой заявки и профиля
                                        applicationRefDB.push().setValue(applicationUser);
                                        userRefDB.push().setValue(newUser);

                                        flag = true;
                                } else {
                                    Toast.makeText(getApplicationContext(),"Вы не выбрали свой пол!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),"Поле дата пишется по правилу: ДД.ММ.ГГГГ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Поле отчество введено некорректно или оно пустое!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),"Поле фамилия введено некорректно или оно пустое!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Поле имя введено некорректно или оно пустое!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (flag) {
            Intent intentUser = new Intent(this, UserActivity.class);
            intentUser.putExtra("stage", 1);
            startActivity(intentUser);
            finish();
        }
    }

    /*
    onClickMen - функция для выбора мужского пола.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальная переменная:
        genderText - переменная для записи половго признака;
    Локальные переменные:
        drawableTrue - стиль нажатой кнопки;
        drawableFalse - стиль не нажатой кнопки;
        colorWhite - код белого цвета;
        colorOrange - код фиолетового цвета;
        buttonMen - кнопка мужского пола;
        buttonWomen - кнопка женского пола.
    */
    public void onClickMen(View view) {
        genderText = "Мужской";

        Drawable drawableTrue = getResources().getDrawable(R.drawable.application_button_gender_false);
        Drawable drawableFalse = getResources().getDrawable(R.drawable.application_button_gender_true);

        int colorWhite = getResources().getColor(R.color.white);
        int colorOrange = Color.parseColor("#858585");

        Button buttonMen = (Button) findViewById(R.id.buttonMen);
        Button buttonWomen = (Button) findViewById(R.id.buttonWomen);

        buttonMen.setBackground(drawableTrue);
        buttonMen.setTextColor(colorWhite);

        buttonWomen.setBackground(drawableFalse);
        buttonWomen.setTextColor(colorOrange);
    }

    /*
    onClickWomen - функция для выбора женского пола.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        genderText - переменная для записи половго признака;
    Локальные переменные:
        drawableTrue - стиль нажатой кнопки;
        drawableFalse - стиль не нажатой кнопки;
        colorWhite - код белого цвета;
        colorOrange - код фиолетового цвета;
        buttonMen - кнопка мужского пола;
        buttonWomen - кнопка женского пола.
    */
    public void onClickWomen(View view) {
        genderText = "Женский";

        Drawable drawableTrue = getResources().getDrawable(R.drawable.application_button_gender_false);
        Drawable drawableFalse = getResources().getDrawable(R.drawable.application_button_gender_true);

        int colorWhite = getResources().getColor(R.color.white);
        int colorOrange = Color.parseColor("#858585");

        Button buttonMen = (Button) findViewById(R.id.buttonMen);
        Button buttonWomen = (Button) findViewById(R.id.buttonWomen);

        buttonWomen.setBackground(drawableTrue);
        buttonWomen.setTextColor(colorWhite);

        buttonMen.setBackground(drawableFalse);
        buttonMen.setTextColor(colorOrange);
    }

    /*
    onClickPhoto - функция для запуска получения фотографий с телефона.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Функция, используемая в форме:
        getImage - хранилище телефона.
    */
    public void onClickPhoto(View view) {
        getImage();
    }

    /*
    onClickUser - функция для закрытия текущего окна.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    */
    public void onClickUser(View view) {
        finish();
    }

    /*
    onActivityResult - функция при нажатии на imageView.
    Формальные параметры:
        requestCode - запрос в хранилище;
        resultCode - ответ хранилища;
        data - окно хранилища.
    Глобальная переменная:
        imageView - переменная для отображения фотографий пользователя.
    Локальные переменные:
        frameLayout - лист отображающий заявку;
        imageButton - кнопка с картинкой;
        textView - текст отображающийся в кнопке.
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            if (resultCode == RESULT_OK) {
                FrameLayout frameLayout = findViewById(R.id.frameLayoutPhoto);
                ImageButton imageButton = findViewById(R.id.imageButtonPhoto);
                TextView textView = findViewById(R.id.imageTextPhoto);
                imageView = findViewById(R.id.imageViewDocument);

                imageView.setImageURI(data.getData());

                //Отображение загруженной картинки с хранилища телефона
                imageButton.setElevation(1);
                imageView.setElevation(2);
                textView.setElevation(1);
            }
        }
    }

    /*
    getImage - хранилище телефона.
    Локальная переменная:
        intentChooser - окно хранилища телефона.
    */
    private void getImage() {
        Intent intentChooser = new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentChooser, 1);
    }

    /*
    uploadImage - отправка фоторгафии в хранилище.
    Формальный параметр:
        callback - параметр для ожидания изменения переменных.
    Глобальные переменные:
        imageView - переменная для отображения фотографий пользователя;
        uploadUri - переменная для записи ссылки фотографии;
        storageReference - получение ссылки в хранилище картинок.
    Локальные переменные:
        bitMap - карта битов;
        baos - поток байтов;
        byteArray - массив байтов;
        mStorageReference - ссылка на фотографию;
        up - обновление картинки;
        task - запрос в онлайн хранилище;
    */
    private void uploadImage(CallbackImage.Callback callback) {
        imageView = findViewById(R.id.imageViewDocument);
        //Преобразование картинки в массив байтов
        Bitmap bitMap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray() ;

        //Загрузка картинки в хранилище
        StorageReference mStorageReference = storageReference.child(System.currentTimeMillis() + "image_application");
        UploadTask up = mStorageReference.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mStorageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadUri = task.getResult().toString();
                if (callback != null) {
                    callback.onCallbackImage(uploadUri);
                }
            }
        });
    }
}