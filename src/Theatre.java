import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Theatre {

    class Seat implements Comparable<Seat>{

        private String seatNum;
        private boolean isReserved;

        public Seat(char row, int column, boolean isReserved) {

            seatNum = "%c%03d".formatted(row, column).toUpperCase();
            this.isReserved = isReserved;
        }
        public Seat(char row, int column) {
            this(row, column, false);
        }

        @Override
        public String toString() {
            return seatNum;
        }

        @Override
        public int compareTo(Seat o) {
            return seatNum.compareTo(o.seatNum);
        }
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";

    private String theatreName;
    private int seatsPerRow;
    NavigableSet<Seat> seats;

    public Theatre(String theatreName, int rows, int totalSeats) {
        this.theatreName = theatreName;

        if(rows < 0) {
            rows = Math.abs(rows);
        }
        if(rows > 26) {
            rows = 26;
        }
        if(totalSeats < 0) {
            totalSeats = Math.abs(totalSeats);
        }
        this.seatsPerRow = totalSeats/rows;

        seats = new TreeSet<>();
        for (int i = 0; i < totalSeats; i++) {
            char rowChar = (char) (i / seatsPerRow + (int) 'A');
            int seatInRow = i % seatsPerRow + 1;
            seats.add(new Seat(rowChar, seatInRow));
        }
    }

    public void printSeats() {

        String separatorLine = "-".repeat(120);
        System.out.printf("%1$s%nMap of Theatre \"%2$s\" seats:%n%1$s%n",
                separatorLine, theatreName);

        int index = 1;
        for (var i : seats) {
            String color = i.isReserved ? ANSI_RED : ANSI_GREEN;
            System.out.printf("%s%-7s%s", color, i, ANSI_RESET);
            if(index++ % seatsPerRow == 0) System.out.println();
        }
        System.out.println(separatorLine);
    }

    private NavigableSet<Seat> getFreeSeats(char row, int minColumn, int maxColumn) {

        Seat minSeat = new Seat(row, minColumn);
        Seat maxSeat = new Seat(row, maxColumn);

        NavigableSet<Seat> set = new TreeSet<>(seats.subSet(minSeat, true,
                maxSeat, true));

        NavigableSet<Seat> set2 = new TreeSet<>(set);
        for(var i : set) {
            if(i.isReserved) set2 = set.tailSet(i, true);
        }
        return set2;
    }

    public void reserveSeats(int tickets, char minRow, int minColumn,
                             char maxRow, int maxColumn, boolean isContiguous) {

        if (!validate(new Seat(minRow, minColumn))) return;
        if (!validate(new Seat(maxRow, maxColumn))) return;

        List<NavigableSet<Seat>> sets = new ArrayList<>((int) maxRow - (int) minRow);
        for(char i = minRow; i <= maxRow; i++) {
            sets.add(getFreeSeats(i, minColumn, maxColumn));
        }
        NavigableSet<Seat> sub = null;
        if(isContiguous) {
            for(var i : sets) {
                if(i.size() == tickets) {
                    sub = i;
                    break;
                }   else if(i.size() > tickets) {
                    sub = i.headSet(new Seat(i.first().seatNum.charAt(0), minColumn+tickets), false);
                    break;
                }
            }
        } else {
            sub = seats.subSet(new Seat(minRow, minColumn), true,
                    new Seat(maxRow, maxColumn), true);
            int reservedSeatsInSub = 0;
            for(var i : sub) {
                if(i.isReserved) reservedSeatsInSub++;
            }
            if(tickets > sub.size()-reservedSeatsInSub) {
                System.out.println("No places available!");
                return;
            }
        }
        if(sub == null) {
            System.out.println("No places available!");
            return;
        }
        for(var i : sub) {
            seats.floor(i).isReserved = true;
        }
    }

    public void reserveSeats(int tickets, char minRow, int minColumn,
                             char maxRow, int maxColumn) {
        reserveSeats(tickets, minRow, minColumn, maxRow, maxColumn, true);
    }

    public void reserveSeats(int tickets, char minRow, int minSeat) {

        String lastSeat = seats.last().seatNum;
        char row = lastSeat.charAt(0);
        int column = Integer.parseInt(lastSeat.substring(1));
        reserveSeats(tickets, minRow, minSeat, row, column);
    }

    private boolean validate(Seat seat) {

        Seat seat2 = seats.floor(seat);

        if (seat2 == null || !seat2.seatNum.equals(seat.seatNum)) {
            System.out.println("There is no such seat: " + seat);
            System.out.printf("Seat must be between %s and %s%n",
                    seats.first(), seats.last());
            return false;
        }
        return true;
    }

    public void reserve(char row, int column) {

        Seat seat = new Seat(row, column);
        if (!validate(seat)) return;

        Seat requested = seats.floor(seat);
        assert requested != null;
        if (requested.isReserved) {
            System.out.printf("Seat %s is already reserved!%n", requested);
            return;
        }
        requested.isReserved = true;
        System.out.printf("Seat %s reserved successfully!%n", requested);
    }

}
