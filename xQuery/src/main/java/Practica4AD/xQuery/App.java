package Practica4AD.xQuery;

import java.util.Objects;
import java.util.Scanner;

import javax.xml.transform.OutputKeys;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;


public class App {
	private static String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
	private static String USER = "admin";
	private static String PASSWORD = "admin";

	private static XQueryService xquery;

	public static void main(String[] args) {

		final String driver = "org.exist.xmldb.DatabaseImpl";

		
		Class cl = null;
		try {
			cl = Class.forName(driver);
			Database database = (Database) cl.newInstance();
			database.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(database);

			Collection col = null;
			XMLResource res = null;

			// get the collection
			col = DatabaseManager.getCollection(URI, USER, PASSWORD);
			col.setProperty(OutputKeys.INDENT, "no");
			xquery = Objects.requireNonNull((XQueryService) col.getService(XQueryService.SERVICE_NAME, null));

			System.out.println("Productos:\n" + "1-Número de productos por zona\n"
					+ "2-Denominación de los productos entra las etiquetas <zona10></zona10>...\n"
					+ "3-Producto más caro por zona\n"
					+ "4-Denominación de los productos entra las etiquetas <placa></placa>\n Sucursales:"
					+ "\n5-El código de sucursal tipo pensión\n"
					+ "6-Por cada sucursal: código,director,población,suma del total saldodebe y suma del total saldohaber de sus cuentas."
					+ "\n7-Nombre de los directores,código de sucursal y la población con más de 3 cuentas\n"
					+ "8- Devuelve por cada sucursal, el código de sucursal y los datos de las cuentas con más saldodebe"
					+ "\n9-Devuelve la cuenta del tipo PENSIONES que ha hecho más aportación");
			
			
			
			
					//1-Número de productos por zona
					System.out.println("1-Número de productos por zona");
					ResourceIterator Iterator = xquery.query(
							"for $v in distinct-values(/productos/produc/cod_zona) return ($v, count(/productos/produc[cod_zona = $v]))")
							.getIterator();

					while (Iterator.hasMoreResources()) {
						System.out.println();
						XMLResource res1 = ((XMLResource) Iterator.nextResource());
						System.out.print("La zona " + res.getContent());
						if (Iterator.hasMoreResources()) {
							XMLResource res2 = ((XMLResource) Iterator.nextResource());
							System.out.println(" tiene " + res2.getContent() + " productos");
						}
					}

					

					
					//2-Denominación de los productos entra las etiquetas 
					System.out.println("2-Denominación de los productos entra las etiquetas");
					ResourceIterator Iterator2 = xquery.query(
							"for $v in distinct-values(/productos/produc/cod_zona) return element{ 'zona' || $v }{ /productos/produc[cod_zona = $v]/denominacion }")
							.getIterator();
					while (Iterator2.hasMoreResources()) {
						System.out.println();
						XMLResource res2 = ((XMLResource) Iterator2.nextResource());
						System.out.println(res2.getContent());
					}

					
					//3-Producto más caro por zona
					System.out.println("3-Producto más caro por zona");
					
					ResourceIterator Iterator3 = xquery.query(
							"for $v in distinct-values(/productos/produc/cod_zona) return ($v, /productos/produc[precio = max(/productos/produc[cod_zona = $v]/precio)]/denominacion/text())")
							.getIterator();
					while (Iterator3.hasMoreResources()) {
						System.out.println();
						XMLResource res3 = ((XMLResource) Iterator3.nextResource());
						System.out.print("En la zona " + res3.getContent());
						if (Iterator3.hasMoreResources()) {
							XMLResource res2 = ((XMLResource) Iterator3.nextResource());
							System.out.println(", el producto más caro es " + res2.getContent());
						}
					}

					
					//4-Denominación de los productos entra las etiquetas <placa></placa>\n Sucursales:
					System.out.println("4-Denominación de los productos entra las etiquetas <placa></placa>\\n Sucursales:");
					ResourceIterator Iterator4 = xquery
							.query("(<placa>{/productos/produc/denominacion[contains(., 'Placa Base')]}</placa>,"
									+ "<micro>{/productos/produc/denominacion[contains(., 'Micro')]}</micro>,"
									+ "<memoria>{/productos/produc/denominacion[contains(., 'Memoria')]}</memoria>,"
									+ "<otros>{/productos/produc/denominacion[not(contains(., 'Memoria') or contains(., 'Micro') or contains(., 'Placa Base'))]}</otros>)")
							.getIterator();

					while (Iterator4.hasMoreResources()) {
						System.out.println();
						XMLResource res4 = ((XMLResource) Iterator4.nextResource());
						System.out.println(res4.getContent());
					}


					//5-El código de sucursal tipo pensión
					System.out.println("5-El código de sucursal tipo pensión");
					ResourceIterator Iterator5 = xquery.query(
							"for $suc in /sucursales/sucursal return (data($suc/@codigo), count($suc/cuenta[data(@tipo)='AHORRO']), count($suc/cuenta[data(@tipo)='PENSIONES']))")
							.getIterator();
					while (Iterator5.hasMoreResources()) {
						System.out.println();
						XMLResource res5 = ((XMLResource) Iterator5.nextResource());
						System.out.print("La sucursal " + res5.getContent());

						if (Iterator5.hasMoreResources()) {
							XMLResource res52 = (XMLResource) Iterator5.nextResource();
							System.out.print(" tiene " + res52.getContent() + " cuentas tipo AHORRO y ");
						}

						if (Iterator5.hasMoreResources()) {
							XMLResource res53 = (XMLResource) Iterator5.nextResource();
							System.out.println(res53.getContent() + " cuentas tipo PENSIONES");
						}
					}
					
					//6-Por cada sucursal: código,director,población,suma del total saldodebe y suma del total saldohaber de sus cuentas.
					System.out.println("6-Por cada sucursal: código,director,población,suma del total saldodebe y suma del total saldohaber de sus cuentas.");
					ResourceIterator Iterator6 = xquery.query(
							"for $suc in /sucursales/sucursal return (data($suc/@codigo), $suc/director/text(), $suc/poblacion/text(), sum($suc/cuenta/saldodebe), sum($suc/cuenta/saldohaber))")
							.getIterator();

					while (Iterator6.hasMoreResources()) {
						System.out.println();

						XMLResource res6 = (XMLResource) Iterator6.nextResource();
						System.out.println("Código: " + res6.getContent());

						if (Iterator6.hasMoreResources()) {
							res6 = (XMLResource) Iterator6.nextResource();
							System.out.println("Director: " + res6.getContent());
						}

						if (Iterator6.hasMoreResources()) {
							res6 = (XMLResource) Iterator6.nextResource();
							System.out.println("Población: " + res6.getContent());
						}

						if (Iterator6.hasMoreResources()) {
							res6 = (XMLResource) Iterator6.nextResource();
							System.out.println("Total saldodebe: " + res6.getContent());
						}

						if (Iterator6.hasMoreResources()) {
							res6 = (XMLResource) Iterator6.nextResource();
							System.out.println("Total saldohaber: " + res6.getContent());
						}
					}

					
					//7-Nombre de los directores,código de sucursal y la población con más de 3 cuentas
					System.out.println("7-Nombre de los directores,código de sucursal y la población con más de 3 cuentas");
					ResourceIterator Iterator7 = xquery.query(
							"for $suc in /sucursales/sucursal[count(cuenta) > 3] return (data($suc/@codigo), $suc/director/text(), $suc/poblacion/text())")
							.getIterator();

					while (Iterator7.hasMoreResources()) {
						System.out.println();

						XMLResource res7 = (XMLResource) Iterator7.nextResource();
						System.out.println("Código: " + res.getContent());

						if (Iterator7.hasMoreResources()) {
							res7 = (XMLResource) Iterator7.nextResource();
							System.out.println("Director: " + res7.getContent());
						}

						if (Iterator7.hasMoreResources()) {
							res7 = (XMLResource) Iterator7.nextResource();
							System.out.println("Población: " + res7.getContent());
						}
					}

					//8- Devuelve por cada sucursal, el código de sucursal y los datos de las cuentas con más saldodebe
					System.out.println("8- Devuelve por cada sucursal, el código de sucursal y los datos de las cuentas con más saldodebe");
					ResourceIterator Iterator8 = xquery.query(
							"for $suc in /sucursales/sucursal return (data($suc/@codigo), $suc/cuenta[saldodebe = max($suc/cuenta/saldodebe)]/*/text())")
							.getIterator();

					while (Iterator8.hasMoreResources()) {
						System.out.println();

						XMLResource res8 = (XMLResource) Iterator8.nextResource();
						System.out.println("Cuenta con más saldodebe de la sucursal " + res8.getContent());

						if (Iterator8.hasMoreResources()) {
							res8 = (XMLResource) Iterator8.nextResource();
							System.out.println("Nombre: " + res8.getContent());
						}

						if (Iterator8.hasMoreResources()) {
							res8 = (XMLResource) Iterator8.nextResource();
							System.out.println("Número: " + res8.getContent());
						}

						if (Iterator8.hasMoreResources()) {
							res8 = (XMLResource) Iterator8.nextResource();
							System.out.println("Saldohaber: " + res8.getContent());
						}

						if (Iterator8.hasMoreResources()) {
							res8 = (XMLResource) Iterator8.nextResource();
							System.out.println("Saldodebe: " + res8.getContent());
						}
					}

					//9-Devuelve la cuenta del tipo PENSIONES que ha hecho más aportación
					System.out.println("9-Devuelve la cuenta del tipo PENSIONES que ha hecho más aportación");
					ResourceIterator Iterator9 = xquery.query(
							"/sucursales/sucursal/cuenta[data(@tipo) = 'PENSIONES' and aportacion = max(/sucursales/sucursal/cuenta/aportacion)]")
							.getIterator();

					while (Iterator9.hasMoreResources()) {
						System.out.println();

						XMLResource res9 = (XMLResource) Iterator9.nextResource();
						System.out.println(res9.getContent());
					}

					
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (XMLDBException e) {
			e.printStackTrace();
		}

	}

}
