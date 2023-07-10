/*
Класс: ListUsers
Язык: Java
Краткое описание:
Класс отображющий всех жильцов общежития.

Глобальные переменные:
    binding - перемнная для обращения к объектам данного окна;
    listAdapter - адаптивный список жильцов;
    userDB - инициализация базы данных пользователей;
    applicationDB - инициализация базы данных приложения;
    db - подключение к базе данных комнат;
    userArrayList - список жильцов отображающийся в окне;
    userKick - фрагмент для выселения жильца;
    userKickInfo - данные выселяемого пользователя.

Функции, используемая в форме:
    onCreate – функция для вызова формы "activty_user_list";
    onClickBack - закрытие текущего окна;
    onClickYes - функция для выселения жильца;
    onClickNo - функция для отмены выселения жильца.
*/
package com.hfad.mydiplomapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hfad.mydiplomapp.classApp.Application;
import com.hfad.mydiplomapp.classApp.ListAdapter;
import com.hfad.mydiplomapp.classApp.User;
import com.hfad.mydiplomapp.databinding.ActivityListUsersBinding;

import java.util.ArrayList;

public class ListUsers extends AppCompatActivity {
    private ActivityListUsersBinding binding;
    private ListAdapter listAdapter;
    private DatabaseReference userDB;
    private DatabaseReference applicationDB;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<User> userArrayList = new ArrayList<>();
    private FrameLayout userKick;
    private TextView userKickInfo;

    /*
    onCreate - функция для запуска формы "activity_list_user".
    Формальный параметр:
        savedInstanceState - параметр для восстановления состояния активности, в случае необходимости.
    Глобальные параметры:
        binding - перемнная для обращения к объектам данного окна;
        listAdapter - адаптивный список жильцов;
        userDB - инициализация базы данных пользователей;
        applicationDB - инициализация базы данных приложения;
        userArrayList - список жильцов отображающийся в окне;
        userKick - фрагмент для выселения жильца;
        userKickInfo - данные выселяемого пользователя.
    Локальные переменные:
        view - пременная для работы с объектами окна;
        user - переменная для получения данных из БД пользователей;
        listView - список отображающий всех жильцов.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListUsersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Найти frameLayout userKick в макете и сохранить ссылку на него в поле userKick
        userKick = view.findViewById(R.id.kickUser);
        userKickInfo = view.findViewById(R.id.user_info_kick);

        // Создать ListAdapter, передав оба параметра, включая userKick
        listAdapter = new ListAdapter(ListUsers.this, userArrayList, userKick, userKickInfo);
        ListView listView = binding.listView2;
        listView.setAdapter(listAdapter);

        // Получение ссылок для подключения к базам данных
        userDB = FirebaseDatabase.getInstance().getReference("User");
        applicationDB = FirebaseDatabase.getInstance().getReference("Application");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userArrayList.size() > 0) {
                    userArrayList.clear();
                }
                for (DataSnapshot ds: snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getRoom() != null) {
                        userArrayList.add(user);
                    }
                }
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*
    onClickBack - закрытие текущего окна.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    */
    public void onClickBack(View view) {
        finish();
    }

    /*
    onClickYes - функция для выселения жильца.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    Глобальные переменные:
        listAdapter - адаптивный список жильцов;
        userDB - инициализация базы данных пользователей;
        applicationDB - инициализация базы данных приложения;
        userArrayList - список жильцов отображающийся в окне;
        userKick - фрагмент для выселения жильца.
    Локальные переменные:
        valueEventListener - слушатель для базы данных заявок;
        valueEventListener1 - слушатель для базы данных пользователей;
        user - получние данных о выселяемом пользователе;
        application - получение данных из БД заявок;
        user1 - получение данных из БД пользователей;
        roomsCollectionRef - получение ссылки на базу данных комнат;
        roomsDocumentReferences - переменная для запросов в БД;
        f_1 - койка "3_1";
        f_2 - койка "3_2";
        f_3 - койка "3_3";
        t_1 - койка "2_1".
    */
    public void onClickYes(View view) {
        User user = listAdapter.userSelected;

        ValueEventListener applicationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Application application = ds.getValue(Application.class);
                    if (user.getUserId().equals(application.getUserId())) {
                        ds.getRef().child("applicationStage").setValue(3);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListUsers.this, "Ошибка при обновлении статуса заявки", Toast.LENGTH_SHORT).show();
            }
        };

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user1 = ds.getValue(User.class);
                    if (user1.getUserId().equals(user.getUserId())) {
                        ds.getRef().child("room").setValue(null)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        applicationDB.addListenerForSingleValueEvent(applicationListener);
                                    }
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListUsers.this, "Ошибка при удалении пользователя", Toast.LENGTH_SHORT).show();
            }
        };

        //Запуск слушателя
        userDB.addListenerForSingleValueEvent(userListener);
        applicationDB.addListenerForSingleValueEvent(applicationListener);

        CollectionReference roomsCollectionRef = db.collection("rooms");
        DocumentReference roomsDocumentReferences = roomsCollectionRef.document(user.getRoom());

        //Запрос в базу данных комнат
        roomsDocumentReferences.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String f_1 = documentSnapshot.getString("3_1");
                    String f_2 = documentSnapshot.getString("3_2");
                    String f_3 = documentSnapshot.getString("3_3");
                    String t_1 = documentSnapshot.getString("2_1");

                    //Выселенеи из комнаты проживающего
                    if (f_1.equals(user.getUserId())) {
                        roomsDocumentReferences.update("3_1", "✖");
                    } else if (f_2.equals(user.getUserId())) {
                        roomsDocumentReferences.update("3_2", "✖");
                    } else if (f_3.equals(user.getUserId())) {
                        roomsDocumentReferences.update("3_3", "✖");
                    } else if (t_1.equals(user.getUserId())) {
                        roomsDocumentReferences.update("2_1", "✖");
                    } else {
                        roomsDocumentReferences.update("2_2", "✖");
                    }
                }
            }
        });

        Toast.makeText(getApplicationContext(),"Пользователь выселен!", Toast.LENGTH_SHORT).show();

        userKick.setVisibility(View.GONE);
    }

    /*
    onClickNo - функция для отмены выселения жильца.
    Формальный параметр:
        view - параметр, который указывает на какой объект требуется обработать нажатие.
    */
    public void onClickNo(View view) {
        userKick.setVisibility(View.GONE);
    }
}