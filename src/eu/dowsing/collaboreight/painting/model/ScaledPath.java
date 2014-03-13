package eu.dowsing.collaboreight.painting.model;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

public class ScaledPath extends Path {
	
	private Path actualPath = new Path();

	private float scale = 1.0f;
	
	private double offsetX = 0;
	private double offsetY = 0;
	
	private String name = null;
	
	/**
	 * Create a new scaled path.
	 */
	public ScaledPath(){
		super();
	}
	
	
	/**
	 * Copy a scaled path.
	 * @param path
	 */
	public ScaledPath(ScaledPath path, String name){
		super(path);
		actualPath = path.getActualPath();
		
		this.scale = path.getScale();
		this.offsetX = path.getOffsetX();
		this.offsetY = path.getOffsetY();
		this.name = name;
	}
	
	/**
	 * Get the actual path the user has touched.
	 * @return
	 */
	public Path getActualPath(){
		return this.actualPath;
	}
    
    public Path getCurrentScaled(float scale){
    	Path scaled = new ScaledPath();
    	// computed scaled path
    	Matrix scaleMatrix = new Matrix();
    	scaleMatrix.setScale(scale, scale, 0, 0);
    	// get scaled path through transform
    	transform(scaleMatrix, scaled);
    	return scaled;
    }

	@Override
	public String toString(){
		RectF rect = new RectF();
    	computeBounds(rect, true);
    	
		return "ScaledPath "+name+" with area "+rect;
	}
	
	/**
	 * Set the offsite of the path when created.
	 * 
	 * @param x
	 * @param y
	 */
	public void setOffset(double x, double y){
		this.offsetX = x;
		this.offsetY = y;
	}
	
	public double getOffsetX(){
		return this.offsetX;
	}
	
	public double getOffsetY(){
		return this.offsetY;
	}
	
	/**
	 * Set the scale of the current path.
	 * @param scale
	 */
	public void setScale(float scale){
		this.scale = scale;
	}
	
	public float getScale(){
		return this.scale;
	}
	
	@Override
	public void reset(){
		super.reset();
		actualPath.reset();
	}
	
	@Override
	public void lineTo(float x, float y){
		actualPath.lineTo(x, y);
		
		x /= scale;
		y /= scale;
		
		super.lineTo(x, y);
	}
	
	@Override
	public void moveTo(float x, float y){
		actualPath.moveTo(x, y);
		
		x /= scale;
		y /= scale;
		
		super.moveTo(x, y);
	}
	
	@Override
	public void quadTo(float x1, float y1, float x2, float y2){
		actualPath.quadTo(x1, y1, x2, y2);
		
		x1 /= scale;
		y1 /= scale;
		x2 /= scale;
		y2 /= scale;
		
		super.quadTo(x1, y1, x2, y2);
	}
	
}
