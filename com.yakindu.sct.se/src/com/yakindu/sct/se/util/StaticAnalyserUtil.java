package com.yakindu.sct.se.util;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

/**
 * Utility class to traverse EObject and search for specific objects
 * 
 * @author jwielage
 *
 */
public class StaticAnalyserUtil {

	@SuppressWarnings("unchecked")
	public static <T extends EObject> void findAllInTree(EObject source, Class<T> clazz, boolean cancelAfterFind,
			Consumer<T> action) {
		TreeIterator<EObject> iter = source.eAllContents();
		while (iter.hasNext()) {
			EObject obj = iter.next();
			if (clazz.isInstance(obj)) {
				action.accept((T) obj);
				if (cancelAfterFind) {
					return;
				}
			}
		}
	}

	/**
	 * To use this, you have to cast the EObject in the consumer yourself, like: v
	 * -> ((Vertex)v) and then do your stuff
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void multiplefindAllInTree(EObject source, List<Class<? extends EObject>> clazzList,
			boolean cancelAfterFind, List<Consumer<EObject>> actions) {

		if (clazzList == null || actions == null) {
			return;
		}
		if (clazzList.size() != actions.size()) {
			return;
		}

		TreeIterator<EObject> iter = source.eAllContents();
		while (iter.hasNext()) {
			EObject obj = iter.next();

			for (int i = 0; i < clazzList.size(); i++) {
				if (clazzList.get(i).isInstance(obj)) {

					actions.get(i).accept(clazzList.get(i).cast(obj));
					if (cancelAfterFind) {
						return;
					}
					break;
				}

			}
		}
	}

}
