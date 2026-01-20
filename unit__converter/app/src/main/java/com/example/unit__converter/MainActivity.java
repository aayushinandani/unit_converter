public class ConverterFragment extends Fragment {
    private FragmentConverterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConverterBinding.inflate(inflater, container, false);

        binding.convertButton.setOnClickListener(v -> performConversion());

        return binding.getRoot();
    }

    private void performConversion() {
        String inputStr = binding.inputValue.getText().toString();
        if (inputStr.isEmpty()) return;

        double value = Double.parseDouble(inputStr);
        String fromUnit = binding.fromSpinner.getSelectedItem().toString();
        String toUnit = binding.toSpinner.getSelectedItem().toString();
        double result = 0;

        // Example: Length Logic
        if (fromUnit.equals("Meters") && toUnit.equals("Kilometers")) {
            result = value / 1000;
        } else if (fromUnit.equals("Meters") && toUnit.equals("Miles")) {
            result = value * 0.000621371;
        }

        // Example: Temperature Logic
        else if (fromUnit.equals("Celsius") && toUnit.equals("Fahrenheit")) {
            result = (value * 9/5) + 32;
        }

        binding.resultText.setText(String.format("%.2f %s", result, toUnit));
    }
}