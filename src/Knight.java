public class Knight extends ChessPiece
{
    public Knight(Board theBoard, int row, int col) 
    {
        super(theBoard, row, col);
        side = true;
        this.imagePath = ChessPiece.filePathHeader+"\\Images\\-Knight.png";
        super.setImagePath(this.imagePath);
    }
    @Override
    public boolean canMoveHere(int row, int col)
    {
        boolean A = Math.abs(this.getRow()-row)==2;
        boolean B = Math.abs(this.getRow()-row)==1;
        boolean C = Math.abs(this.getCol()-col)==1;
        boolean D = Math.abs(this.getCol()-col)==2;
        return exclusiveOr((A&&C),(B&&D));
    }
    //Does not Override boolean ChessPiece.move(). The Knight's movement is unique in this program
    //because it does not flow through a logical chart. Instead, the player controls its movements.
    public double move(Coordinates dest)
    {
        double capturedBounty = 0;
        if(this.canMoveHere(dest.row(), dest.col()))
        {
            if (MyBoard.getPieceAt(dest.row(), dest.col()) instanceof ChessPiece
                    && !MyBoard.getPieceAt(dest.row(), dest.col()).getSide())
                capturedBounty = MyBoard.getPieceAt(dest.row(), dest.col()).getBounty();
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), dest.row(), dest.col());
        }
        return capturedBounty;
    }
}