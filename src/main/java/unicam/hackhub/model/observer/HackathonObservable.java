package unicam.hackhub.model.observer;

public interface HackathonObservable {

    void addObserver(HackathonObserver observer);

    void removeObserver(HackathonObserver observer);

    void notifyObservers();

}
