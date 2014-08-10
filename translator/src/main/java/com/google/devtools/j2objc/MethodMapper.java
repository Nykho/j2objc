package com.google.devtools.j2objc;

import com.google.common.io.Files;
import com.google.devtools.j2objc.types.JavaMethod;
import com.google.devtools.j2objc.types.Types;
import com.google.devtools.j2objc.util.ErrorUtil;
import com.google.devtools.j2objc.util.JdtParser;
import com.google.devtools.j2objc.util.NameTable;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class MethodMapper {

  public static void main(String[] args) throws IOException {
    String[] files = null;
    try {
      files = Options.load(args);
      if (files.length == 0) {
        Options.usage("no source files");
      }
    } catch (IOException e) {
      ErrorUtil.error(e.getMessage());
      System.exit(1);
    }
    NameTable.initialize();

    for (int i = 0; i < files.length; i++) {
      File file = new File(files[i]);
      JdtParser parser = J2ObjC.createParser();

      final CompilationUnit cu = (CompilationUnit) parser.parse(files[i] , Files.toString(file, Charset.forName("UTF-8")));
      Types.initialize(cu);

      cu.accept(new ASTVisitor() {

        public boolean visit(org.eclipse.jdt.core.dom.MethodDeclaration node) {
          IMethodBinding binding = Types.getMethodBinding(node);
          JavaMethod desc = JavaMethod.getJavaMethod(binding);
          //if(!methodMappings.containsKey(desc.getKey()) && desc.getClazz().startsWith("com.laundrapp")  && node.parameters().size() > 0) {
          StringBuilder method = new StringBuilder("LA").append(binding.getDeclaringClass().getName()).append(" ").append(node.isConstructor() ? "init" : node.getName());
          boolean first = true;
          for (SingleVariableDeclaration parameter : (List<SingleVariableDeclaration>)node.parameters()) {
            if(!first && !parameter.getName().equals(Types.EMPTY_PARAMETER_NAME)) {
              method.append(parameter.getName());
            }
            method.append(String.format(":(%s)%s ",
                NameTable.getSpecificObjCType(Types.getTypeBinding(parameter.getType())), parameter.getName()));
            first = false;
          }
          System.out.println(desc.getKey() +  " = " + method.toString());
          //}

          return false; // do not continue to avoid usage info
        }

      });

    }
  }
}
