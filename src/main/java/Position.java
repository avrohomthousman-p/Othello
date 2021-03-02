//Avrohom Tzvi Housman

/**I copy/pasted this class from the previous homework. I kept the documentation.
 *
 * This class is used to reference a specific position in a 2 dimentinal array
 * of any object. In next homework I plan to use it to point to an array of JPanels
 * that change color during a game of Othello. Some methods in the Othello interface
 * return a list of these objects. Each element of that list points to a specific
 * JPanel that needs to change color.
 */
public class Position {
    public int i;
    public int j;

    public Position(){
        i = -1;
        j = -1;
    }

    public Position(int i, int j){
        this.i = i;
        this.j = j;
    }

    public Position(Position template){
        this.i = template.i;;
        this.j = template.j;
    }

    @Override
    public String toString(){
        return String.format("(%d, %d)", i, j);
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;

        if(this.getClass() == o.getClass()){
            Position other = (Position)  o;
            return this.i == other.i && this.j == other.j;
        }
        return false;
    }

    @Override
    public int hashCode(){
        int hash = 11;
        hash = hash * 13 + i;
        hash = hash * 13 + j;
        return hash;
    }
}
