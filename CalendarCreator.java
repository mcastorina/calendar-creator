import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class CalendarCreator
{
    public static void main(String[] args) throws Exception
    {
        int beginM, beginY, endM, endY;
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the begin date in the form followed by the end date in the form month year");
        System.out.print("Example:\n\tBegin Date: 1 2013\n\tEnd Date: 5 2013\nBegin Date: ");
        String[] begin = in.nextLine().split(" ");
        System.out.print("End Date: ");
        String[] end = in.nextLine().split(" ");

        beginM = Integer.parseInt(begin[0])-1;
        beginY = Integer.parseInt(begin[1]);

        endM = Integer.parseInt(end[0])-1;
        endY = Integer.parseInt(end[1]);

        GregorianCalendar calendar = new GregorianCalendar(beginY, beginM, 1);
        int curYear = calendar.get(GregorianCalendar.YEAR);
        int curMonth = calendar.get(GregorianCalendar.MONTH);

        if (curYear > endY || curYear == endY && curMonth > endM)
            return;

        int numCals = (endY - curYear) * 12 + (endM - curMonth) + 1;
        if (numCals > 12)
        {
            System.out.println("This will generate " + ((numCals/2) + (numCals%2)) + " calendars.");
            System.out.print("Continue? [y/N]: ");
            String resp = in.nextLine();
            if (resp.length() == 0 || resp.toLowerCase().charAt(0) != 'y')
                return;
        }

        ArrayList<Month> months = new ArrayList<Month>();
        do
        {
            months.add(new Month(curYear, curMonth+1, calendar.get(GregorianCalendar.DAY_OF_WEEK)-1));
            calendar.add(GregorianCalendar.MONTH,1);
            curMonth = calendar.get(GregorianCalendar.MONTH);
            curYear = calendar.get(GregorianCalendar.YEAR);
        }
        while (curYear <= endY && (curMonth <= endM || curYear != endY));

        System.out.println("Generating calendars...");

        int boxSizeX = 100;
        int boxSizeY = 70;
        String[] daysOfWeek = "Sunday Monday Tuesday Wednesday Thursday Friday Saturday".split(" ");

        for (int i = 0; i < months.size(); i += 2)
        {
            BufferedImage image = new BufferedImage(850, 1100, BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = image.getGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0,0,850,1100);
            g.setColor(Color.BLACK);

            boolean[] days = months.get(i).getDays();

            for (int t = 0; t < 8; ++t)
            {
                g.drawLine(75+(t*boxSizeX), 85, 75+(t*boxSizeX), 515);
                if (t < 7)
                    g.drawString(daysOfWeek[t],75+(t*boxSizeX)+5, 90);
            }
            for (int t = 0; t < 7; ++t)
                g.drawLine(75, 95+(t*boxSizeY), 775, 95+(t*boxSizeY));

            g.drawString(months.get(i).getMonth(), 75, 60);
            g.drawString(""+months.get(i).getYear(), 749, 60);

            int day = 1;
            for (int p = 0; p < days.length; ++p)
            {
                if (days[p])
                    g.drawString(""+day++, 75+(p%7)*boxSizeX+5, 95+(p/7)*boxSizeY+15);
            }
            // g.drawRect(75, 95, 700, 420);

            if (i+1 < months.size())
            {
                // g.drawRect(75, 605, 700, 420);
                days = months.get(i+1).getDays();

                for (int t = 0; t < 8; ++t)
                {
                    g.drawLine(75+(t*boxSizeX), 595, 75+(t*boxSizeX), 1025);
                    if (t < 7)
                        g.drawString(daysOfWeek[t],75+(t*boxSizeX)+5, 600);
                }
                for (int t = 0; t < 7; ++t)
                    g.drawLine(75, 605+(t*boxSizeY), 775, 605+(t*boxSizeY));

                g.drawString(months.get(i+1).getMonth(), 75, 570);
                g.drawString(""+months.get(i+1).getYear(), 749, 570);

                day = 1;
                for (int p = 0; p < days.length; ++p)
                {
                    if (days[p])
                        g.drawString(""+day++, 75+(p%7)*boxSizeX+5, 605+(p/7)*boxSizeY+15);
                }
            }

            g.dispose();
            ImageIO.write(image,"png",new File("cal"+i/2+".png"));
        }
    }
}
class Month
{
    private int year, month;
    boolean[] days;

    public Month(int y, int m, int firstDay)
    {
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
    public int getYear()
    {
        return year;
    }
    public String getMonth()
    {
        return "January February March April May June July August September October November December".split(" ")[month-1];
    }
    public boolean[] getDays()
    {
        return days;
    }
    public static int daysInMonth(int y, int m)
    {
        switch (m)
        {
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
    public static boolean isLeapYear(int y)
    {
        if (y % 400 == 0)
            return true;
        else if (y % 100 == 0)
            return false;
        else if (y % 4 == 0)
            return true;
        return false;
    }
}
