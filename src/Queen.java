import java.util.ArrayList;
 
public class Queen extends ChessPiece
{
    public Queen(Board theBoard, int row, int col) 
    {
        super(theBoard, row, col);
        side = false;
        bounty = ChessPiece.QUEEN_BOUNTY;
        this.imagePath = ChessPiece.filePathHeader+"\\Images\\Queen.png";
        super.setImagePath(this.imagePath);
    }
    @Override
    public boolean canMoveHere(int row, int col)
    {
        boolean A = this.getRow()==row && horizontalPathClear(col);
        boolean B = this.getCol()==col && verticalPathClear(row);
        boolean C = Math.abs(this.getRow()-row) == Math.abs(this.getCol()-col) && diagonalPathClear(row,col);
        boolean D = MyBoard.getPieceAt(row, col) instanceof ChessPiece ? MyBoard
                .getPieceAt(row, col).getSide() : true;
        return (exclusiveOr(exclusiveOr(A,B),C)&&D);
    }
    @Override
    public boolean move()
    {
        int KR = 0, KC = 0;
        try{
            KR = MyBoard.findKnight().getRow();
            KC = MyBoard.findKnight().getCol();
        }catch(NullPointerException e){
        }
         
        if(this.canMoveHere(KR,KC))                         //First priority for any ChessPiece is to capture
        {                                                   //the Knight.
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), KR, KC);
            return true;                                    
        }
        else
        {   //The Queen can "check" the Knight vertically and horizontally, so it randomly picks
            //between the two possible moves.
            if(this.canMoveHere(this.getRow(), KC) && this.canMoveHere(KR,this.getCol()))
            {
                int rand = (int)((Math.random()*100)+1);
                if(rand>50)
                    MyBoard.moveFromTo(this, this.getRow(), this.getCol(), this.getRow(), KC);
                else
                    MyBoard.moveFromTo(this, this.getRow(), this.getCol(), KR, this.getCol());
                return true;
            }
            else if(this.canMoveHere(KR,this.getCol()))
            {
                MyBoard.moveFromTo(this, this.getRow(), this.getCol(), KR, this.getCol());
                return true;                        //The Queen can check the Knight after a vertical move.
            }
            else if(this.canMoveHere(this.getRow(), KC))
            {
                MyBoard.moveFromTo(this, this.getRow(), this.getCol(), this.getRow(), KC);
                return true;                        //The Queen can check the Knight after a horizontal move.
            }
            else
            {                                       //When all else fails, it moves diagonally to "check".
                ArrayList<Coordinates> diagonals = new ArrayList<Coordinates>();
                try{
                    ChessPiece target = MyBoard.findKnight();
                    diagonals = target.getDiagonals();
                }catch(NullPointerException e){
                }
                 
                if(diagonals.isEmpty())
                    return false;
                 
                for(int i=0; i<diagonals.size();++i)
                    if(!this.canMoveHere(diagonals.get(i).row(), diagonals.get(i).col()))
                    {
                        diagonals.remove(i);
                        i-=1;
                    }
                if(diagonals.isEmpty())
                    return false;
                 
                int rand = (int)(Math.random()*diagonals.size());
                MyBoard.moveFromTo(this, this.getRow(), this.getCol(), diagonals
                        .get(rand).row(), diagonals.get(rand).col());
            }
            return false;
        }
    }  
}