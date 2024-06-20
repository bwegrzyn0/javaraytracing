package ray;

import math.*;

public class Sphere {

  public Vector3 position;
  public float radius;
  public int color;

  public Sphere(Vector3 position, float radius, int color) {
    this.position = position;
    this.radius = radius;
    this.color = color;
  }

  public float rayIntersection(Ray ray) {
    Vector3 tempVec = new Vector3(position);
    tempVec.add(ray.origin.multiplied(-1));
    float h = ray.dir.dot(tempVec);
    float c = tempVec.dot(tempVec) - radius * radius;
    float delta = h * h - c;
    return (delta >= 0) ? (h - (float) Math.sqrt(delta)) : -1f;
  }
}
