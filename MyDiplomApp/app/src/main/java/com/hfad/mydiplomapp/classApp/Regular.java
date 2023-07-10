package com.hfad.mydiplomapp.classApp;

public class Regular {
    public boolean isMail(String mail) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (mail.matches(emailRegex)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPassword(String password) {
        String passwordRegex = "^[A-Za-z0-9]{8,13}$";
        if (password.matches(passwordRegex)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFio(String fio) {
        String fioRegex = "^[a-zA-Zа-яА-Я]{1,15}$";
        if (fio.matches(fioRegex)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBirthday(String data) {
        String dataRegex =  "^\\d{2}\\.\\d{2}\\.\\d{4}$";
        if (data.matches(dataRegex)) {
            return true;
        } else {
            return false;
        }
    }
}
