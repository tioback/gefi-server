package renatoback.paripassu.teste.analistaIII.gefi.server.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import renatoback.paripassu.teste.analistaIII.gefi.server.Application;
import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Senha;
import renatoback.paripassu.teste.analistaIII.gefi.server.entity.Tipo;
import renatoback.paripassu.teste.analistaIII.gefi.server.service.SenhaService;

/**
 * @author Josh Long
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SenhaControllerTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private static Senha NORMAL1 = new Senha(Tipo.NORMAL, 1);
	private static Senha NORMAL2 = new Senha(Tipo.NORMAL, 2);
	private static Senha PREFERENCIAL1 = new Senha(Tipo.PREFERENCIAL, 1);

	@SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private SenhaService service;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

		Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		this.service.excluiTodas();
	}

	private void expect(ResultActions teste, Senha senha) throws Exception {
		teste.andExpect(content().contentType(contentType)).andExpect(jsonPath("$.tipo", is(senha.getTipo().name())))
				.andExpect(jsonPath("$.sequencial", is(senha.getSequencial())))
				.andExpect(jsonPath("$.codigo", is(senha.getCodigo())))
				.andExpect(jsonPath("$.prioridade", is(senha.getPrioridade())));
	}

	@Test
	public void geraSenha() throws Exception {
		expect(mockMvc.perform(post("/senha/N")).andExpect(status().isCreated()), NORMAL1);
		expect(mockMvc.perform(post("/senha/P")).andExpect(status().isCreated()), PREFERENCIAL1);
	}

	@Test
	public void chamaProximaSenhaEExisteSenhaNaFila() throws Exception {
		expect(mockMvc.perform(post("/senha/N")).andExpect(status().isCreated()), NORMAL1);
		expect(mockMvc.perform(post("/senha/P")).andExpect(status().isCreated()), PREFERENCIAL1);
		expect(mockMvc.perform(post("/senha")).andExpect(status().isOk()), PREFERENCIAL1);
	}

	@Test
	public void chamaProximaSenhaMasNaoExisteSenhaNaFila() throws Exception {
		mockMvc.perform(post("/senha")).andExpect(status().isNotFound());
	}

	@Test
	public void reiniciaSenha() throws Exception {
		expect(mockMvc.perform(post("/senha/N")).andExpect(status().isCreated()), NORMAL1);
		expect(mockMvc.perform(post("/senha/N")).andExpect(status().isCreated()), NORMAL2);
		mockMvc.perform(put("/senha/N")).andExpect(status().isOk());
		expect(mockMvc.perform(post("/senha/N")).andExpect(status().isCreated()), NORMAL1);
	}

	@Test
	public void acompanhaSenhaMasNenhumaFoiChamada() throws Exception {
		mockMvc.perform(get("/senha")).andExpect(status().isNotFound());
	}

	@Test
	public void acompanhaSenhaEUmaJaFoiChamada() throws Exception {
		expect(mockMvc.perform(post("/senha/N")).andExpect(status().isCreated()), NORMAL1);
		expect(mockMvc.perform(post("/senha")).andExpect(status().isOk()), NORMAL1);
		expect(mockMvc.perform(get("/senha")).andExpect(status().isOk()), NORMAL1);
	}

	@SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}