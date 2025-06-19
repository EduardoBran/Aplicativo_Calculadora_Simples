package com.luizeduardobrandao.calculadorasimplesmvvm.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luizeduardobrandao.calculadorasimplesmvvm.R
import com.luizeduardobrandao.calculadorasimplesmvvm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}

// - Faz binding do layout.
// - Observa as LiveData do ViewModel para atualizar o TextView.
// - Configura os onClickListener dos botões para chamar métodos do ViewModel.
// - Por que? A View só “mostra” e “escuta” dados, sem processar nenhuma regra de cálculo.