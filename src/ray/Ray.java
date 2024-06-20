package ray;

import math.Vector3;

public class Ray {

  public Vector3 dir, origin;
  public int[] color;

  public Ray(Vector3 dir, Vector3 origin) {
    this.dir = dir;
    this.origin = origin;
  }

  public Vector3 pointAt(float t) {
    Vector3 p = new Vector3(origin);
    p.add(dir.multiplied(t));
    return p;
  }
}
