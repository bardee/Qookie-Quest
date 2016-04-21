package maze3d;

/**
 *
 * @author Belcky
 */
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.math.FastMath;

//Class for contrsucting the inside walls of a maze, based on a 2d maze
public class MazeCell extends Node {
    static protected Box wall = new Box(1f,1f,.5f);
    
    MazeCell(Maze maze, Material mat, int row, int col){
        //Constructs horizontal walls (North or South)
        if(maze.hasNorthWall(row, col)){
            Geometry northw = new Geometry("Northwall", wall);
            northw.setMaterial(mat);
            northw.setLocalTranslation(new Vector3f((col*2f)+1.5f,0f,(float)(row*2)-.5f));
            this.attachChild(northw);
        }
        //Constructs vertical walls (East of West)
        if(maze.hasWestWall(row, col)){
            Geometry westw = new Geometry("Westwall", wall);
            westw.setMaterial(mat);
            westw.setLocalTranslation(new Vector3f(col*2f,0f,(float)row*2f));
            westw.rotate(0f, FastMath.PI/2, 0f);
            this.attachChild(westw);
        }
    }
}
