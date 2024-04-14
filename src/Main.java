public class Main {
    public static void main(String[] args) {

        Theatre theatre = new Theatre("Teatr Ludowy", 10, 150);

        theatre.reserve('A', 3);
        theatre.reserve('A', 3);
        theatre.reserve('B', 1);
        theatre.reserve('B', 11);
        theatre.reserve('M', 1);
        theatre.printSeats();

        theatre.reserveSeats(4, 'B', 3, 'B', 10);
        theatre.printSeats();
        theatre.reserveSeats(6, 'B', 3, 'C', 10);
        theatre.printSeats();
    }
}