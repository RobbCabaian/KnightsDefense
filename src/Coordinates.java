//Provides the means for row and column values to be paired together for methods
//such as getDiagonals.
public class Coordinates 
{
    private int row;
    private int col;
      
    public Coordinates(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    public int row()
    {
        return this.row;
    }
    public int col()
    {
        return this.col;
    }
    public void setRow(int row)
    {
        this.row = row;
    }
    public void setCol(int col)
    {
        this.col = col;
    }
}