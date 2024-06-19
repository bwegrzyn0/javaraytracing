package math;

public class Vector3 {

  public float x, y, z;

  public Vector3(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3(Vector3 vector3) {
    x = vector3.x;
    y = vector3.y;
    z = vector3.z;
  }

  public void print() {
    System.out.println("( " + x + ", " + y + ", " + z + " )");
  }

  public void add(Vector3 vector3) {
    x += vector3.x;
    y += vector3.y;
    z += vector3.z;
  }

  public void multiply(float c) {
    x *= c;
    y *= c;
    z *= c;
  }

  public Vector3 multiplied(float c) {
    return new Vector3(x * c, y * c, z * c);
  }

  public Vector3 inversed() {
    return new Vector3(1 / x, 1 / y, 1 / z);
  }

  public void inverse() {
    x = 1 / x;
    y = 1 / y;
    z = 1 / z;
  }

  public float magnitude() {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  public void normalize() {
    float magnitude = magnitude();
    x /= magnitude;
    y /= magnitude;
    z /= magnitude;
  }

  public Vector3 normalized() {
    return new Vector3(x / magnitude(), y / magnitude(), z / magnitude());
  }

  public float dot(Vector3 vector3) {
    return x * vector3.x + y * vector3.y + z * vector3.z;
  }

  public Vector3 cross(Vector3 vector3) {
    float x = this.y * vector3.z - this.z * vector3.y;
    float y = this.z * vector3.x - this.x * vector3.z;
    float z = this.x * vector3.y - this.y * vector3.x;
    return new Vector3(x, y, z);
  }
}
