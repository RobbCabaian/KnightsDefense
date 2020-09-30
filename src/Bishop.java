import java.util.ArrayList;
 
import javax.swing.JOptionPane;
 
public class Bishop extends ChessPiece
{
    public Bishop(Board theBoard, int row, int col) 
    {
        super(theBoard, row, col);
        side=false;
        bounty = ChessPiece.BISHOP_BOUNTY;
        this.imagePath = ChessPiece.filePathHeader+"\\Images\\Bishop.png";
        super.setImagePath(this.imagePath);
    }
    //Requires target Coordinates to have an unobstructed diagonal path and a non-friendly ChessPiece
    //stationed in the given Coordinates.
    @Override
    public boolean canMoveHere(int row, int col)
    {
        boolean A = Math.abs(this.getRow()-row) == Math.abs(this.getCol()-col) && diagonalPathClear(row,col);
        boolean B = MyBoard.getPieceAt(row, col) instanceof ChessPiece ? MyBoard
                .getPieceAt(row, col).getSide() : true;
        return (A&&B);
    }
    @Override
    public boolean move()
    {
        int kr=0, kc=0;
        try {                                       //The Knight could be non-existent in the current Board.
            kr = MyBoard.findKnight().getRow();
            kc = MyBoard.findKnight().getCol();
        }catch(NullPointerException e){
        }       
         
        if(this.canMoveHere(kr,kc))                 //First priority is if it can capture the Knight in
        {                                           //its next move.
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), kr, kc);
            return true;
        }
        else
        {
            ArrayList<Coordinates> diagonals = new ArrayList<Coordinates>();
            try{
                ChessPiece target = MyBoard.findKnight();
                diagonals = target.getDiagonals();
            }catch(NullPointerException e){         //Finds all the diagonals of the Knight.
            }
             
            if(diagonals.isEmpty())                 
                return false;                       //Knight is diagonally "stuck".
             
            for(int i=0; i<diagonals.size();++i)
                if(!this.canMoveHere(diagonals.get(i).row(), diagonals.get(i).col()))
                {
                    diagonals.remove(i);            //Removes Coordinates in the Knight's diagonals which
                    i-=1;                           //can't be moved to by the Bishop.
                }
            if(diagonals.isEmpty())                 
                return false;                       //Bishop cannot "check" the Knight in its next move.
             
            //The Bishop randomly picks a reachable diagonal of the Knight in order to "check".
            int rand = (int)(Math.random()*diagonals.size());
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), diagonals
                    .get(rand).row(), diagonals.get(rand).col());
                 
        }
        return true;
    }
}