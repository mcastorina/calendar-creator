import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

/* Argument parsing library: https://github.com/cbeust/jcommander */
import com.beust.jcommander.*;

public class CalendarCreator {
    @Parameter(names = {"--begin", "-b"}, description = "Begin month and year")
    public String arg_begin;
    @Parameter(names = {"--end", "-e"}, description = "End month and year")
    public String arg_end;
    @Parameter(names = {"--force", "-f"}, description = "Ignore warnings")
    public boolean arg_force = false;
    @Parameter(names = {"--help", "-h"}, description = "Display help message")
    public boolean arg_help = false;

    public static void main(String[] args) throws Exception {
        CalendarCreator cc = new CalendarCreator();
        new JCommander(cc, args);

        cc.run();
    }

    public void run() throws Exception {
        if (arg_begin == null || arg_end == null)
            arg_help = true;
        if (arg_help || ((arg_begin.length() & arg_end.length()) == 0)) {
            String gc = "\n\tGenerate calendars for ";
            String jc = "\t$ java -jar CalendarCreator.jar ";
            System.out.print("Usage: java CalendarCreator [OPTIONS]");
            System.out.println(" (--begin|-b) MM_YYYY (--end|-e) MM_YYYY");
            System.out.println("\nOptions\n");
            System.out.println("\t-f, --force\t\tIgnore warnings");
            System.out.println("\t-h, --help\t\tDisplay this help message");
            System.out.println("\nExamples");
            System.out.println(gc + "through May 2013");
            System.out.println(jc + "--begin 1_2013 --end 5_2013");
            System.out.println(gc + "the year of 2014");
            System.out.println(jc + "--begin 1_2014 --end 12_2014");
            System.out.println(gc + "the years of 2013 and 2014");
            System.out.println(jc + "--force --begin 1_2013 --end 12_2014");
            System.out.println();
            return;
        }

        int beginM, beginY, endM, endY;
        String[] begin = arg_begin.split("_");
        String[] end = arg_end.split("_");

        beginM = Integer.parseInt(begin[0])-1;
        beginY = Integer.parseInt(begin[1]);

        endM = Integer.parseInt(end[0])-1;
        endY = Integer.parseInt(end[1]);

        /* Get the current month and year */
        GregorianCalendar calendar = new GregorianCalendar(beginY, beginM, 1);
        int curYear = calendar.get(GregorianCalendar.YEAR);
        int curMonth = calendar.get(GregorianCalendar.MONTH);

        if (curYear > endY || curYear == endY && curMonth > endM) {
            System.out.println("End date is before begin date.");
            System.out.println("Aborting.");
            return;
        }

        /* Check the number of months we'll be generating */
        int numCals = (endY - curYear) * 12 + (endM - curMonth) + 1;
        if (!arg_force && numCals > 12) {
            Scanner in = new Scanner(System.in);
            System.out.println("This will generate " + ((numCals/2) +
                        (numCals%2)) + " calendars.");
            System.out.print("Continue? [y/N]: ");
            String resp = in.nextLine();
            if (resp.length() == 0 || resp.toLowerCase().charAt(0) != 'y')
                return;
        }

        /* Make ArrayList of months */
        ArrayList<Month> months = new ArrayList<Month>();
        do {
            months.add(new Month(curYear, curMonth+1,
                        calendar.get(GregorianCalendar.DAY_OF_WEEK)-1));
            calendar.add(GregorianCalendar.MONTH,1);
            curMonth = calendar.get(GregorianCalendar.MONTH);
            curYear = calendar.get(GregorianCalendar.YEAR);
        }
        while (curYear <= endY && (curMonth <= endM || curYear != endY));

        /* Render the calendars */
        System.out.println("Generating calendars...");

        int boxSizeX = 100;
        int boxSizeY = 70;
        final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday",
                                    "Wednesday", "Thursday", "Friday",
                                    "Saturday"};

        for (int i = 0; i < months.size(); i += 2) {
            BufferedImage image = new BufferedImage(850, 1100,
                    BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = image.getGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0,0,850,1100);
            g.setColor(Color.BLACK);

            boolean[] days = months.get(i).getDays();

            for (int t = 0; t < 8; ++t) {
                g.drawLine(75+(t*boxSizeX), 85, 75+(t*boxSizeX), 515);
                if (t < 7)
                    g.drawString(daysOfWeek[t],75+(t*boxSizeX)+5, 90);
            }
            for (int t = 0; t < 7; ++t)
                g.drawLine(75, 95+(t*boxSizeY), 775, 95+(t*boxSizeY));

            g.drawString(months.get(i).getMonth(), 75, 60);
            g.drawString(""+months.get(i).getYear(), 749, 60);

            int day = 1;
            for (int p = 0; p < days.length; ++p) {
                if (days[p])
                    g.drawString(""+day++, 75+(p%7)*boxSizeX+5,
                                           95+(p/7)*boxSizeY+15);
            }
            // g.drawRect(75, 95, 700, 420);

            if (i+1 < months.size()) {
                // g.drawRect(75, 605, 700, 420);
                days = months.get(i+1).getDays();

                for (int t = 0; t < 8; ++t) {
                    g.drawLine(75+(t*boxSizeX), 595, 75+(t*boxSizeX), 1025);
                    if (t < 7)
                        g.drawString(daysOfWeek[t],75+(t*boxSizeX)+5, 600);
                }
                for (int t = 0; t < 7; ++t)
                    g.drawLine(75, 605+(t*boxSizeY), 775, 605+(t*boxSizeY));

                g.drawString(months.get(i+1).getMonth(), 75, 570);
                g.drawString(""+months.get(i+1).getYear(), 749, 570);

                day = 1;
                for (int p = 0; p < days.length; ++p) {
                    if (days[p]) {
                        g.drawString(""+day++, 75+(p%7)*boxSizeX+5,
                                               605+(p/7)*boxSizeY+15);
                    }
                }
            }

            g.dispose();
            ImageIO.write(image,"png",new File("cal"+i/2+".png"));
        }
    }
}
class Month {
    private int year, month;
    boolean[] days;

    public Month(int y, int m, int firstDay) {
        year = y;
        month = m;
        int totalDays = daysInMonth(year, month);

        days = new boolean[42];
        int i;
        for (i = 0; i < firstDay; ++i)
            days[i] = false;
        while (i-firstDay < totalDays)
            days[i++] = true;
        while (i < days.length)
            days[i++] = false;
    }
    public int getYear() {
        return year;
    }
    public String getMonth() {
        final String[] months = {"January", "February", "March",
                                "April", "May", "June", "July", "August",
                                "September", "October", "November",
                                "December"};
        return months[month-1];
    }
    public boolean[] getDays() {
        return days;
    }
    public static int daysInMonth(int y, int m) {
        switch (m) {
            case 2:
                if (isLeapYear(y))
                    return 29;
                return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }
    public static boolean isLeapYear(int y) {
        if (y % 400 == 0)
            return true;
        else if (y % 100 == 0)
            return false;
        else if (y % 4 == 0)
            return true;
        return false;
    }
}
