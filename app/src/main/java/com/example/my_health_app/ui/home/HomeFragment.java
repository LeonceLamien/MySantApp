package com.example.my_health_app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_health_app.R;
import com.example.my_health_app.ui.Global;

import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.my_health_app.databinding.FragmentHomeBinding;
import com.example.my_health_app.ui.imc.ImcFragment;
import com.example.my_health_app.ui.stat.StatFragment;
import com.example.my_health_app.ui.step.StepFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    NavController navController;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button workoutsButton = root.findViewById(R.id.button_workouts);
        workoutsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Charge le fragment des entraînements
                /*FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_step, new StepFragment());
                fragmentTransaction.commit();

                 */
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_menu);
                navController.navigate(R.id.nav_step);
            }
        });


        Button bmisButton = root.findViewById(R.id.button_nutrition);
        bmisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Charge le fragment des entraînements
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_menu);
                navController.navigate(R.id.nav_imc);
            }
        });

        Button progressButton = root.findViewById(R.id.button_progress);
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Charge le fragment des entraînements
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_menu);
                navController.navigate(R.id.nav_stat);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}