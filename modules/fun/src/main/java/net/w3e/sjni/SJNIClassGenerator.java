package net.w3e.sjni;

import net.w3e.include.skds.utils.logger.SKDSLogger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.Label;

public class SJNIClassGenerator {

	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();

		load(LolImpl.class);
	}

	public static void load(Class<?> cl) throws IOException {
		String className = cl.getName();
		String classAsPath = className.replace('.', '/') + ".class";
		load(classAsPath);
	}

	@SuppressWarnings("all")
	public static void load(String cl) throws IOException {
		InputStream stream = SJNIClassGenerator.class.getClassLoader().getResourceAsStream(cl);
		ClassModel p = ClassFile.of().parse(stream.readAllBytes());

		StringBuilder sb = new StringBuilder("\nFlags ");
		sb.append(p.flags().flags());
		sb.append("\nsuperclass ").append(p.superclass());

		sb.append("\nfields");
		p.fields().forEach(f -> {

		});
		p.methods().forEach(m -> {
			sb.append("\n\t").append(m);
			sb.append("\n\t\tparent ").append(m.parent());
			sb.append("\n\t\tflags ").append(m.flags().flags());
			m.code().ifPresent(cm -> {
				sb.append("\n\t\tcm ").append(cm);
				cm.elementList().forEach(ce -> {
					if (ce instanceof Label) return;
					sb.append("\n\t\t\t ").append(ce);
				});
			});

		});
		System.out.println(sb);

		//Class<Lol> c = createImpl(Lol.class);
		//System.out.println(c);
		//Lol lol = ReflectUtils.getConstructor(c).get();
		//System.out.println(lol);


		//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		//compiler.run(null, System.out, System.err, sourceFile.getPath());

		//Class<? extends Lol> cl = createImpl(Lol.class);
		//System.out.println(cl);
	}

	private interface Lol {

		int test();

	}

	private static class LolImpl implements Lol {

		@Override
		public int test() {
			int hash = hashCode();
			int inc = 12;
			int result = inc + hash;

			hashCode();

			return result;
		}

		public int test2() {
			try {
				new LolImpl().test();
			} catch (Exception e) {
				System.out.println(e.toString());
			}

			return 0;
		}

		public int test3(int a) {

			test4(3, 0);
			test4();
			return 0;
		}

		public int test4(int... a) {
			return 0;
		}
	}
}