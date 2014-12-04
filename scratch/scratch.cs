using System;

public class scratch
{
    static public void Main()
    {
        Func<int,int> square = x => x * x;
        Console.WriteLine(square(2));
        
        Func<string, string> upperCase = s => s.ToUpper();
        Console.WriteLine(upperCase("tester"));
    }
}
