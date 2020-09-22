package pastebin;

public class Bin {
  String contents;

  public Bin(String contents) {
    super();
    this.contents = contents;
  }

  public String getContents() {
    return contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  @Override
  public String toString() {
    return "Bin [contents=" + contents + "]";
  }
}
