package maindir.observer;

import maindir.exceptions.DAOException;
import maindir.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    private List<Observer> observers;

    protected Subject() {
        this.observers = new ArrayList<>();
    }

    public void attach(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(Appointment appointment) throws DAOException {
        for (Observer observer : observers) {
            observer.update(appointment);
        }
    }
}

