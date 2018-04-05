package diffparser.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CodeExplorerTest {
    String codeSimple = "class X {" +
            "\n  int field;" +
            "\n  void x (){ " +
            "\n    field = 0;" +
            "\n}}";
    CodeExplorer ceSimple = new CodeExplorer(codeSimple);

    String codeMultipleMethods = "package dream.was.so.big;" +
        "\nclass X {" +
        "\n  int field;" +
        "\n  void x (){ " +
        "\n    field = 0;" +
        "\n}" +
        "\n  void y (){ " +
        "\n    field = 0;" +
        "\n}" +
        "\n  void z (){ " +
        "\n    field = 0;" +
        "\n}}";
    CodeExplorer ceMultipleMethods = new CodeExplorer(codeMultipleMethods);

    String codeMultipleClasses = "package get.lucky;\n" +
            "\n" +
            "public class tss {\n" +
            "    int x() {\n" +
            "        return 1;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "class tws {\n" +
            "    int x() {\n" +
            "        return 2;\n" +
            "    }\n" +
            "}";
    CodeExplorer ceMultipleClasses = new CodeExplorer(codeMultipleClasses);

    String codeFields = "class X {" +
            "\n  int fieldDefault;" +
            "\n  public int fieldPublic;" +
            "\n  protected int fieldProtected;" +
            "\n  private int fieldPrivate;" +
            "\n}";
    CodeExplorer ceFields = new CodeExplorer(codeFields);

    List<Integer> modifiedLines;

    @Before
    public void init() {
        modifiedLines = new ArrayList<>();
    }

    @Test
    public void shouldReturnEmptyIfClassNameChanged() {
        modifiedLines.add(1);

        List<String> modified = ceSimple.findModifiedMethods(modifiedLines);
        Assert.assertEquals(0, modified.size());
    }

    @Test
    public void shouldFindOneModifiedFunction() {
        modifiedLines.add(4);

        List<String> modified = ceSimple.findModifiedMethods(modifiedLines);
        Assert.assertEquals(1, modified.size());
        Assert.assertEquals(true, modified.contains("X.x"));
    }

    @Test
    public void shouldFindModifiedFunctionName() {
        modifiedLines.add(3);

        List<String> modified = ceSimple.findModifiedMethods(modifiedLines);
        Assert.assertEquals(1, modified.size());
        Assert.assertEquals(true, modified.contains("X.x"));
    }

    @Test
    public void shouldFindModifiedFunctionLastLine() {
        modifiedLines.add(5);

        List<String> modified = ceSimple.findModifiedMethods(modifiedLines);
        Assert.assertEquals(1, modified.size());
        Assert.assertEquals(true, modified.contains("X.x"));
    }

    @Test
    public void shouldFindMultipleModifiedFunctionsWithPackage() {
        modifiedLines.add(5);
        modifiedLines.add(8);
        modifiedLines.add(11);

        List<String> modified = ceMultipleMethods.findModifiedMethods(modifiedLines);
        Assert.assertEquals(3, modified.size());
        Assert.assertEquals(true, modified.contains("dream.was.so.big.X.x"));
        Assert.assertEquals(true, modified.contains("dream.was.so.big.X.y"));
        Assert.assertEquals(true, modified.contains("dream.was.so.big.X.z"));
    }

    private String noSpacings(String code) {
        return code
                .replace("\n", "")
                .replace(" ", "");
    }

    @Test
    public void shouldReplaceClassNamesForMultiple() {
        ceMultipleMethods.replaceClassNames();
        String replaced = ceMultipleMethods.currentCode();

        String expectedLines = codeMultipleMethods.replaceAll("X", "X" + CodeExplorer.CLASS_MODIFIER);

        Assert.assertEquals(noSpacings(expectedLines), noSpacings(replaced));
    }

    @Test
    public void shouldReplaceClassNamesForMultipleClasses() {
        ceMultipleClasses.replaceClassNames();
        String replaced = ceMultipleClasses.currentCode();

        String expectedLines = codeMultipleClasses.replaceAll("tss", "tss" + CodeExplorer.CLASS_MODIFIER)
                .replaceAll("tws", "tws" + CodeExplorer.CLASS_MODIFIER);

        Assert.assertEquals(noSpacings(expectedLines), noSpacings(replaced));
    }

    @Test
    public void shouldMakeFieldsPublic() {
        ceFields.makeFieldsPublic();

        String expected = codeFields
                .replace("int fieldDefault", "public int fieldDefault")
                .replace("protected int", "public int")
                .replace("private int", "public int");

        Assert.assertEquals(noSpacings(expected), noSpacings(ceFields.currentCode()));
    }
}
