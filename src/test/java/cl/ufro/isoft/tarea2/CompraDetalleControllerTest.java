package cl.ufro.isoft.tarea2;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import cl.ufro.isoft.tarea2.indicador.IndicadorClient;
import cl.ufro.isoft.tarea2.model.Unidad;

@SpringBootTest(properties = "app.csv-path=src/test/resources/compras-test.csv")
class CompraDetalleControllerTest {

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private IndicadorClient indicadorClient;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	void detalleDeCompraEnUf() throws Exception {
		given(indicadorClient.valor(Unidad.UF, LocalDate.of(2026, 9, 4))).willReturn(39010.25);
		// 8 * 39010.25 = 312082
		mockMvc.perform(get("/compras/detalle/3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3))
			.andExpect(jsonPath("$.producto").value("Escritorio"))
			.andExpect(jsonPath("$.monto").value(8.0))
			.andExpect(jsonPath("$.unidad").value("UF"))
			.andExpect(jsonPath("$.fecha").value("2026-09-04"))
			.andExpect(jsonPath("$.valor_unidad").value(39010.25))
			.andExpect(jsonPath("$.monto_pesos").value(312082));
	}

	@Test
	void detalleDeCompraEnDolar() throws Exception {
		given(indicadorClient.valor(Unidad.DOLAR, LocalDate.of(2026, 9, 8))).willReturn(945.55);
		// 95.9 * 945.55 = 90678.245 -> 90678
		mockMvc.perform(get("/compras/detalle/4"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.unidad").value("USD"))
			.andExpect(jsonPath("$.monto_pesos").value(90678));
	}

	@Test
	void compraInexistenteResponde404() throws Exception {
		mockMvc.perform(get("/compras/detalle/999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.mensaje").value("No existe una compra con id 999"));
	}

	@Test
	void idNoNumericoResponde400() throws Exception {
		mockMvc.perform(get("/compras/detalle/abc")).andExpect(status().isBadRequest());
	}

}
