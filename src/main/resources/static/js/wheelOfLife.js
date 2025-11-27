document.addEventListener('DOMContentLoaded', () => {
    const labels = areas.map(a => a.name);
    const values = areas.map(a => a.rate);

    const ctx = document.getElementById('wheelChart').getContext('2d');

    const hueStart = 35;
    const hueEnd = 55;
    const saturation = 70;
    const alpha = 0.65;

    const backgroundColors = labels.map((_, i) => {
        const hue = hueStart + i * ((hueEnd - hueStart) / (labels.length - 1));
        const lightness = 45 + (i * (25 / labels.length));  // delikatnie rośnie
        return `hsla(${hue}, ${saturation}%, ${lightness}%, ${alpha})`;
    });

    const borderColors = labels.map((_, i) => {
        const hue = hueStart + i * ((hueEnd - hueStart) / (labels.length - 1));
        const lightness = 30 + (i * (20 / labels.length));
        return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
    });

    new Chart(ctx, {
        type: 'polarArea',
        data: {
            labels: labels,
            datasets: [{
                label: 'Poziom zadowolenia',
                data: values,
                fill: true,
                backgroundColor: backgroundColors,
                borderColors: borderColors
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                r: {
                    min: 0,
                    max: 10,
                    ticks: {
                        stepSize: 1,
                        showLabelBackdrop: false,
                        color: 'goldenrod'
                    },
                    grid: {
                        color: 'rgba(218, 165, 32, 0.5)',
                        lineWidth: 0.5
                    },
                }
            },
            plugins: { legend: { display: true } }
        }
    });
})